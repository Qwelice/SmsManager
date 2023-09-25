package ru.qwelice.smsmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.TelephonyManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.qwelice.smsmanager.mailing.MailClient

class SmsReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var onReceive: (String, String) -> Unit = { _, _ -> }
    fun onReceiveAction(action: (String, String) -> Unit){
        onReceive = action
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action?.compareTo("android.provider.Telephony.SMS_RECEIVED", true) == 0) {
            context?.run{
                val tel = getSystemService(TelephonyManager::class.java)
                val smss = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                smss?.let{
                    val subject = "Android Sms Receiver"
                    val msg = it.fold(""){ full, part -> "$full${part.displayMessageBody}" }
                    scope.launch {
                        MailClient.getInstance()?.onReceive?.invoke(subject, msg)
                    }
                }
            }
        }
    }
}