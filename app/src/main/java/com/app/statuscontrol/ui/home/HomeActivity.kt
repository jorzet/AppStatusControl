package com.app.statuscontrol.ui.home

import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.app.statuscontrol.databinding.ActivityHomeBinding
import com.app.statuscontrol.domain.model.LoginAction
import com.app.statuscontrol.domain.model.Notification
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.ui.login.LoginActivity
import com.app.statuscontrol.utils.click
import com.app.statuscontrol.utils.showOffline
import com.app.statuscontrol.utils.showOnline
import com.app.statuscontrol.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class HomeActivity: AppCompatActivity(), TextToSpeech.OnInitListener {
    // Binding
    private lateinit var binding: ActivityHomeBinding

    // View model
    private val viewModel: HomeViewModel by viewModels()

    // Receiver
    private lateinit var mNetworkChangeReceiver: NetworkChangeReceiver

    // Text to speech
    private lateinit var textToSpeech: TextToSpeech

    private var isConnected: Boolean = true


    companion object {
        const val HOME_ACTION = "home_action"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setUpComponents()
        setUpListeners()
        initObservers()
        //registerNetworkBroadcastForNougat()

        supportFragmentManager
            .beginTransaction()
            .add(binding.fragmentContainerView.id, LaneStatusFragment())
            .commit()
    }

    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }

    private fun setUpComponents() {
        textToSpeech = TextToSpeech(this, this)
        viewModel.getUserLane()
        viewModel.getNotification()
    }

    private fun initObservers() {
        viewModel.userLaneState.observe(this) { state ->
            when(state) {
                is Resource.Success -> {
                    binding.welcomeTextView.text = state.data.lane
                    viewModel.getLane(state.data)
                }
                else -> Unit
            }
        }

        viewModel.laneState.observe(this) { state ->
            when(state) {
                is Resource.Success -> {
                    viewModel.saveLaneToLocal(state.data)
                    if (state.data.status) {
                        binding.onlineImageView.showOnline()
                    } else {
                        binding.onlineImageView.showOffline()
                    }
                }
                else -> Unit
            }
        }

        viewModel.modifyLaneState.observe(this) { state ->
            when(state) {
                is Resource.Success -> {
                    if (state.data.status) {
                        binding.onlineImageView.showOnline()
                    } else {
                        binding.onlineImageView.showOffline()
                    }
                }
                else -> Unit
            }
        }
        viewModel.getNotificationLaneState.observe(this) { state ->
            when(state) {
                is Resource.Success -> {
                    showNotification(state.data)
                }
                else -> Unit
            }
        }
    }

    private fun registerNetworkBroadcastForNougat() {
        mNetworkChangeReceiver = NetworkChangeReceiver(object : ConnectionNetworkListener {
            override fun onConnected() {
                binding.onlineImageView.showOnline()
                showError("Conectado")
                isConnected = true
            }
            override fun onDisconnected() {
                binding.onlineImageView.showOffline()
                showError("Desconectado")
                isConnected = false
            }
        })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(
                mNetworkChangeReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(
                mNetworkChangeReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
    }

    fun showUserStatus() {
        supportFragmentManager
            .beginTransaction()
            .add(binding.fragmentContainerView.id, UserStatusFragment())
            .addToBackStack("user_status_fragment")
            .commit()
    }

    private fun setUpListeners() {
        binding.closeApp click {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra(LoginActivity.LOGIN_ACTION, LoginAction.LOGIN_ACTION)
            this.finish()
            startActivity(intent)

            viewModel.logout()
        }
        binding.btnChangeStatus click {
            viewModel.changeLaneStatus()
        }
    }

    fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    private fun showNotification(notification: Notification) {

        val text = "La ${notification.lane} ${if (notification.status)"esta" else "no esta"} disponible"

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
                .setSmallIcon(com.app.statuscontrol.R.mipmap.ic_launcher_round) //                        .setContentTitle(getString(R.string.app_name)
                .setContentTitle(notification.lane) // title for notification
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
            channel.lockscreenVisibility = VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 , notificationBuilder.build())

        speakSomething(text)
    }

    private fun speakSomething(message: String) {
        textToSpeech.stop()
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null );
    }

    /**
     * Connection Listener
     */
    interface ConnectionNetworkListener {
        fun onConnected()
        fun onDisconnected()
    }

    /**
     * Connection BroadcastReceiver
     */
    class NetworkChangeReceiver(): BroadcastReceiver() {
        private lateinit var mConnectionNetworkListener: ConnectionNetworkListener

        constructor(connectionNetworkListener: ConnectionNetworkListener): this() {
            mConnectionNetworkListener = connectionNetworkListener
        }

        override fun onReceive(context: Context?, p1: Intent?) {
            try {
                val cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val netInfo = cm.activeNetworkInfo
                val isConnected = netInfo != null && netInfo.isConnected
                if (isConnected) {
                    mConnectionNetworkListener.onConnected()
                } else {
                    mConnectionNetworkListener.onDisconnected()
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()

            }
        }
    }

    override fun onInit(status: Int) {
        if ( status == TextToSpeech.SUCCESS ) {
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