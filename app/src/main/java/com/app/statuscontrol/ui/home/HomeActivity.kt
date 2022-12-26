package com.app.statuscontrol.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.statuscontrol.databinding.ActivityHomeBinding
import com.app.statuscontrol.utils.click
import com.app.statuscontrol.utils.showOffline
import com.app.statuscontrol.utils.showOnline
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity: AppCompatActivity() {
    // Binding
    private lateinit var binding: ActivityHomeBinding

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
        setUpListeners()
        registerNetworkBroadcastForNougat()

        supportFragmentManager
            .beginTransaction()
            .add(binding.fragmentContainerView.id, LaneStatusFragment())
            .commit()
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
            finish()
        }
    }

    fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
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
}