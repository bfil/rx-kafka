package io.bfil.rx.kafka.config

import java.util.Properties

import scala.concurrent.duration.{Duration, FiniteDuration}

import com.typesafe.config.Config

sealed trait ConfigType
object ConfigTypes {
  case object Int extends ConfigType
  case object Bytes extends ConfigType
  case object String extends ConfigType
  case object Boolean extends ConfigType
  case object Duration extends ConfigType
  case object FiniteDuration extends ConfigType
}

class PropertiesBuilder(config: Config) {

  implicit class RichConfig(config: Config) {
    def getDuration(key: String): Duration = {
      val stringifiedDuration = config.getString(key)
      val duration = Duration.create(if(stringifiedDuration == "infinite") "Inf" else stringifiedDuration)
      if (!duration.isFinite || duration.toMillis <= 0) Duration.Inf
      else duration
    }
    def getFiniteDuration(key: String): FiniteDuration = {
      val duration = getDuration(key)
      if (duration.isFinite) FiniteDuration(duration.length, duration.unit)
      else throw new IllegalArgumentException(s"$key must be finite")
    }
  }

  def build(configs: Map[String, (String, ConfigType)]): Properties = {
    val props = new Properties()
    configs.map {
      case (propKey, (configKey, configType)) =>
        if (config.hasPath(configKey)) {
          configType match {
            case ConfigTypes.Int     => props.put(propKey, config.getInt(configKey).toString)
            case ConfigTypes.Bytes   => props.put(propKey, config.getBytes(configKey).toString)
            case ConfigTypes.String  => props.put(propKey, config.getString(configKey))
            case ConfigTypes.Boolean => props.put(propKey, config.getBoolean(configKey).toString)
            case ConfigTypes.Duration => {
              val d = config.getDuration(configKey)
              props.put(propKey, if (d.isFinite) d.toMillis.toString else "-1")
            }
            case ConfigTypes.FiniteDuration => props.put(propKey, config.getFiniteDuration(configKey).toMillis.toString)
          }
        }
    }
    props
  }
}