package ru.qwelice.smsmanager.mailing

import jakarta.mail.Message
import jakarta.mail.MessagingException
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeBodyPart
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeMultipart
import ru.qwelice.smsmanager.SmsReceiver
import ru.qwelice.smsmanager.mailing.enums.PortType
import java.util.*

class MailClient private constructor(
    private val config: MailConfiguration,
    val onReceive: (String, String) -> Unit){
    companion object{
        private var lastInstance: MailClient? = null
        fun create(config: MailConfiguration, onReceive: (String, String) -> Unit) : MailClient{
            lastInstance = MailClient(config, onReceive)
            return lastInstance!!
        }

        fun getInstance() : MailClient? = lastInstance
    }

    private val auth = MailAuthenticator(config.username, config.password)

    suspend fun sendMessage(message: MailMessage) : Boolean{
        val props = Properties()
        props["mail.smtp.host"] = config.host
        props["mail.smtp.port"] = config.port
        props["mail.smtp.auth"] = true
        if(config.portType == PortType.SSL){
            props["mail.smtp.ssl.enable"] = true
            props["mail.smtp.socketFactory.port"] = config.port
            props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
            props["mail.smtp.socketFactory.fallback"] = false
        } else{
            props["mail.smtp.starttls.enable"] = true
        }

        val session = Session.getInstance(props, auth)
        try{
            val msg = MimeMessage(session)
            msg.setFrom(InternetAddress(message.from))
            msg.setRecipient(Message.RecipientType.TO, InternetAddress(message.to))
            msg.subject = message.subject
            msg.sentDate = Date()

            val textBody = MimeBodyPart()
            textBody.setText(
                "${message.content}\n\n" +
                        "Координаты телефона (широта долгота): ${message.location}",
                "utf-8"
            )

            val multiPart = MimeMultipart()
            multiPart.addBodyPart(textBody)

            msg.setContent(multiPart)
            Transport.send(msg)
            return true
        } catch (mex: MessagingException){
            mex.printStackTrace()
            var ex: Exception?
            if (mex.nextException.also { ex = it } != null) {
                ex!!.printStackTrace()
            }
            return false
        }
    }
}