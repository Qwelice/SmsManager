package ru.qwelice.smsmanager.mailing

import ru.qwelice.smsmanager.mailing.enums.HostType
import ru.qwelice.smsmanager.mailing.enums.PortType

class MailConfigurationBuilder {
    private val configSource = HashMap<String, Any>()
    fun host(host: HostType) : MailConfigurationBuilder{
        when(host){
            HostType.Yandex -> configSource["host"] = "smtp.yandex.ru"
            else -> configSource["host"] = "smtp.gmail.com"
        }
        return this
    }

    fun port(port: PortType) : MailConfigurationBuilder{
        configSource["port-type"] = port
        when(port){
            PortType.SSL -> configSource["port"] = 465
            else -> configSource["port"] = 587
        }
        return this
    }

    fun email(email: String) : MailConfigurationBuilder{
        configSource["email"] = email
        return this
    }

    fun username(username: String) : MailConfigurationBuilder{
        configSource["username"] = username
        return this
    }

    fun password(password: String) : MailConfigurationBuilder{
        configSource["password"] = password
        return this
    }

    fun buildYandexPreset(username: String, password: String, email: String): MailConfiguration {
        val host = "smtp.yandex.ru"
        val port = 465
        val portType = PortType.SSL

        configSource["host"] = host
        configSource["port"] = port
        configSource["port-type"] = portType
        configSource["username"] = username
        configSource["password"] = password
        configSource["email"] = email

        return build()
    }

    fun build() : MailConfiguration{
        val checkedKeys = configSource.containsKey("host")
                && configSource.containsKey("port")
                && configSource.containsKey("port-type")
                && configSource.containsKey("username")
                && configSource.containsKey("password")
                && configSource.containsKey("email")
        if(!checkedKeys){
            throw IllegalArgumentException("configuration source must contain all properties")
        }
        val checkedValues = configSource["host"] != null
                && configSource["port"] != null
                && configSource["port-type"] != null
                && configSource["username"] != null
                && configSource["password"] != null
                && configSource["email"] != null
        if(!checkedValues){
            throw IllegalArgumentException("configuration source must contain non-null properties")
        }
        val host = configSource["host"] as String
        val port = configSource["port"] as Int
        val portType = configSource["port-type"] as PortType
        val username = configSource["username"] as String
        val password = configSource["password"] as String
        val email = configSource["email"] as String
        return MailConfiguration(
            host, port, portType, email, username, password
        )
    }
}