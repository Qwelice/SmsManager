package ru.qwelice.smsmanager.mailing

data class MailMessage(
    val from: String,
    val to: String,
    val subject: String,
    val content: String,
    val location: String = ""
)