package ru.qwelice.smsmanager.mailing

import ru.qwelice.smsmanager.mailing.enums.PortType

data class MailConfiguration(
    val host: String,
    val port: Int,
    val portType: PortType,
    val email: String,
    val username: String,
    val password: String
)