package ru.qwelice.smsmanager.models

import ru.qwelice.smsmanager.mailing.MailClient
import ru.qwelice.smsmanager.mailing.MailConfiguration
import ru.qwelice.smsmanager.mailing.MailMessage

class MailModel {
    private var client: MailClient? = null

    fun setConfiguration(config: MailConfiguration, onReceive: (String, String) -> Unit){
        client = MailClient.create(config, onReceive)
    }

    suspend fun sendMessage(msg: MailMessage) : Boolean {
        if(client == null){
            return false
        }
        return client!!.sendMessage(msg)
    }
}