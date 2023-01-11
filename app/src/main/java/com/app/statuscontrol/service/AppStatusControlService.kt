package com.app.statuscontrol.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.statuscontrol.R
import com.app.statuscontrol.data.util.FirebaseConstants
import com.app.statuscontrol.domain.model.Notification
import com.app.statuscontrol.ui.home.HomeActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.util.*


class AppStatusControlService: Service(), TextToSpeech.OnInitListener  {
    companion object {
        const val NOTIFICATION_BR = "com.app.statuscontrol.service"
        const val NOTIFICATION_SUCEESS = "notification_success"
        const val NOTIFICATION_ERROR = "notification_error"
        const val NOTIFICATION_STATUS = "notification_status"
        private const val DELAY_MILLIS_SPEAK: Long = 2000
    }

    /*
     * Broadcast intent
     */
    private var bi = Intent(NOTIFICATION_BR)

    private lateinit var subscription: ListenerRegistration

    // Text to speech
    private lateinit var textToSpeech: TextToSpeech

    var startMode = 0 // indicates how to behave if the service is killed
    var binder: IBinder? = null // interface for clients that bind
    var allowRebind = false

    override fun onCreate() {
        super.onCreate()
        textToSpeech = TextToSpeech(this, this)

        val event: Query = FirebaseFirestore.getInstance()
            .collection(FirebaseConstants.NOTIFICATION_COLLECTION)
            .orderBy("lastModification", Query.Direction.DESCENDING)
            .limit(1)


        subscription = event.addSnapshotListener { snapshot, error ->

            if (error != null) {
                bi.putExtra(NOTIFICATION_ERROR, true)
                sendBroadcast(bi)
                //this.trySend(Resource.Error(error.message.toString())).isSuccess
                //return@addSnapshotListener
            }

            if (snapshot != null) {

                var notificationStatus = Notification()
                snapshot.documents.map {
                    val notification = it.toObject(Notification::class.java)
                    notification?.let { not ->
                        notificationStatus = not
                        notification.id = it.id
                    }
                }

                val text = "La ${notificationStatus.lane} ${if (notificationStatus.status)"esta" else "no esta"} disponible"

                val intent = Intent(applicationContext, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                val pendingIntent = PendingIntent.getActivity(
                    this, 0, intent,
                    PendingIntent.FLAG_IMMUTABLE
                )

                val channelId = "some_channel_id"
                val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val notificationBuilder: NotificationCompat.Builder =
                    NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.libros_icon_png) //                        .setContentTitle(getString(R.string.app_name)
                        .setContentTitle(notificationStatus.lane) // title for notification
                        .setContentText(text) // message for notification
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentIntent(pendingIntent)

                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

                // Since android Oreo notification channel is needed.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    channel.lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                    notificationManager.createNotificationChannel(channel)
                }

                notificationManager.notify(0 , notificationBuilder.build())
                Thread.sleep(DELAY_MILLIS_SPEAK)
                speakSomething(text)
                //bi.putExtra(NOTIFICATION_SUCEESS, true)
                //bi.putExtra(NOTIFICATION_STATUS, notificationStatus)
                //sendBroadcast(bi)
                //this.trySend(Resource.Success(notificationStatus)).isSuccess
            } else {
                bi.putExtra(NOTIFICATION_SUCEESS, false)
                sendBroadcast(bi)
            }
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val CHANNELID = "Foreground Service ID"
        val channel = NotificationChannel(
            CHANNELID,
            CHANNELID,
            NotificationManager.IMPORTANCE_LOW
        )

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        val notification: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNELID)
            .setContentText("app is running in background")
            .setContentTitle("AppStatusControl")
            .setSmallIcon(R.drawable.libros_icon_png)

        startForeground(1001, notification.build())
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder;
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
        return allowRebind;
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::subscription.isInitialized)
            subscription.remove()

        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    private fun speakSomething(message: String) {
        textToSpeech.stop()
        textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null );
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS ) {
            val result = textToSpeech.setLanguage( Locale.getDefault() );

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                Log.d("TTS", "TTS ready")
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }
}