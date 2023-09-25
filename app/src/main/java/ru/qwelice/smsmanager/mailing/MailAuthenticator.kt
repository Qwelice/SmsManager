package ru.qwelice.smsmanager.mailing

import jakarta.mail.Authenticator
import jakarta.mail.PasswordAuthentication

class MailAuthenticator(private val username: String, private val password: String) : Authenticator() {
    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(username, password)
    }
}