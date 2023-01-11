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
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.app.statuscontrol.R
import com.app.statuscontrol.databinding.ActivityHomeBinding
import com.app.statuscontrol.domain.model.*
import com.app.statuscontrol.service.AppStatusControlService
import com.app.statuscontrol.ui.login.LoginActivity
import com.app.statuscontrol.utils.*
import com.app.statuscontrol.viewmodel.HomeViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.messaging.ktx.remoteMessage
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


@AndroidEntryPoint
class HomeActivity: AppCompatActivity(){
    // Binding
    private lateinit var binding: ActivityHomeBinding

    // View model
    private val viewModel: HomeViewModel by viewModels()

    // Receiver
    private lateinit var mNetworkChangeReceiver: NetworkChangeReceiver

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
        //checkSystemWritePermission()
        startForegroundService(Intent(this, AppStatusControlService::class.java))
        startReceiver()

        Firebase.messaging.token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token)
            Log.d("FCM", msg)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })

        Firebase.messaging.subscribeToTopic("notification_status")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Log.d("FCM", msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
        sendUpstream()
    }

    fun deviceGroupUpstream() {
        // [START fcm_device_group_upstream]
        val to = "a_unique_key" // the notification key
        val msgId = AtomicInteger()
        Firebase.messaging.send(remoteMessage(to) {
            setMessageId(msgId.get().toString())
            addData("hello", "world")
        })
        // [END fcm_device_group_upstream]
    }

    fun sendUpstream() {
        val SENDER_ID = "383305896074"
        val messageId = 0 // Increment for each
        // [START fcm_send_upstream]
        val fm = Firebase.messaging
        fm.send(remoteMessage("$SENDER_ID@fcm.googleapis.com") {
            setMessageId(messageId.toString())
            addData("my_message", "Hello World")
            addData("my_action", "SAY_HELLO")
        })
        // [END fcm_send_upstream]
    }

    private fun checkSystemWritePermission(): Boolean {
        var retVal = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(this)
            Log.d("TAG", "Can Write Settings: $retVal")
            if (retVal) {
                ///Permission granted by the user
            } else {
                //permission not granted navigate to permission screen
                openAndroidPermissionsMenu()
            }
        }
        return retVal
    }

    private fun openAndroidPermissionsMenu() {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.data = Uri.parse("package:" + this.packageName)
        startActivity(intent)
    }

    private fun setUpComponents() {
        viewModel.getUser()
        //viewModel.getNotification()
    }

    private fun initObservers() {
        viewModel.userLaneState.observe(this) { state ->
            when(state) {
                is Resource.Loading -> {
                    handleLoading(true)
                }
                is Resource.Success -> {
                    binding.welcomeTextView.text = state.data.lane
                    viewModel.getLane(state.data)
                    handleLoading(false)
                }
                else -> Unit
            }
        }

        viewModel.laneState.observe(this) { state ->
            when(state) {

                is Resource.Loading -> {
                    handleLoading(true)
                }
                is Resource.Success -> {
                    viewModel.saveLaneToLocal(state.data)
                    if (state.data.status) {
                        binding.onlineImageView.showOnline()
                    } else {
                        binding.onlineImageView.showOffline()
                    }
                    handleLoading(false)
                }
                else -> Unit
            }
        }

        viewModel.modifyLaneState.observe(this) { state ->
            when(state) {
                is Resource.Loading -> {
                    handleLoading(true)
                }
                is Resource.Success -> {
                    if (state.data.status) {
                        binding.onlineImageView.showOnline()
                    } else {
                        binding.onlineImageView.showOffline()
                    }
                    handleLoading(false)
                }
                else -> Unit
            }
        }
        viewModel.getNotificationLaneState.observe(this) { state ->
            when(state) {
                is Resource.Loading -> {
                    handleLoading(true)
                }
                is Resource.Success -> {
                    showNotification(state.data)
                    handleLoading(false)
                }
                else -> Unit
            }
        }
        viewModel.userTypeState.observe(this) { user ->
            user?.let {
                showLaneStatus(user)
                binding.welcomeTextView.text = "Bienvenido ${user.name}"
                when(user.userType) {
                    UserType.ADMIN.userType -> {
                        binding.onlineImageView.setGone()
                        binding.btnChangeStatus.setGone()
                        binding.btnAddNewLane.setVisible()
                    }
                    UserType.CONSUMER.userType -> {
                        binding.onlineImageView.setGone()
                        binding.btnChangeStatus.setGone()
                        binding.btnAddNewLane.setGone()
                    }
                    UserType.EMPLOYEE.userType -> {
                        binding.onlineImageView.setVisible()
                        binding.btnChangeStatus.setVisible()
                        binding.btnAddNewLane.setGone()
                    }
                }
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

    fun showCreateNewLane() {
        supportFragmentManager
            .beginTransaction()
            .add(binding.fragmentContainerView.id, CreateLaneFragment())
            .addToBackStack("create_new_lane")
            .commit()
        binding.btnAddNewLane.setGone()
    }

    fun showEditLane(laneStatus: LaneStatus, user: User) {
        val createNewLane = CreateLaneFragment()
        createNewLane.setLaneStatus(laneStatus)
        createNewLane.setCurrentUser(user)
        supportFragmentManager
            .beginTransaction()
            .add(binding.fragmentContainerView.id, createNewLane)
            .addToBackStack("create_new_lane")
            .commit()
        binding.btnAddNewLane.setGone()
    }

    fun showLaneStatus(user: User) {
        supportFragmentManager
            .beginTransaction()
            .add(binding.fragmentContainerView.id, LaneStatusFragment(user))
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
        binding.btnAddNewLane click {
            showCreateNewLane()
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

    /*
     * This method just register the BroadcastReceiver
     */
    private fun startReceiver() {
        try {
            if (notificationBroadCast != null) {
                this.registerReceiver(notificationBroadCast, IntentFilter(AppStatusControlService.NOTIFICATION_BR))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } catch (e: kotlin.Exception) {
            e.printStackTrace()
        }
    }

    /*
     * this method unregister the BroadcastReceiver and change flag to identify
     * that all images are downloaded
     */
    private fun stopReceiver() {
        try {
            if (notificationBroadCast != null) {
                this.unregisterReceiver(notificationBroadCast)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } catch (e: kotlin.Exception) {
            e.printStackTrace()
        }
    }

    private val notificationBroadCast = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.extras != null) {
                if (intent.getBooleanExtra(AppStatusControlService.NOTIFICATION_SUCEESS,false)) {
                    val notification: Notification = intent.getSerializableExtra(AppStatusControlService.NOTIFICATION_STATUS) as Notification
                   showNotification(notification)
                }
            }
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
               loading.setVisible()
                fragmentContainerView.setGone()
                buttonsContainer.setGone()
            } else {
                loading.setGone()
                fragmentContainerView.setVisible()
                buttonsContainer.setVisible()
            }
        }
    }

    fun getUserInfo() {
        viewModel.getUser()
    }
}