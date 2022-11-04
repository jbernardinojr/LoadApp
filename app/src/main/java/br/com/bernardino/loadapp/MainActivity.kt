package br.com.bernardino.loadapp

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.com.bernardino.loadapp.customviews.ButtonState
import br.com.bernardino.loadapp.customviews.LoadingButton
import br.com.bernardino.loadapp.databinding.ActivityMainBinding
import br.com.bernardino.loadapp.extensions.isOptionSelected
import br.com.bernardino.loadapp.extensions.sendNotification

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var loadingButton: LoadingButton
    private lateinit var downloadOptionsRadioGroup: RadioGroup
    private var requestDownloadID = 0L

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (requestDownloadID == id) {
                if (intent.action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    loadingButton.state = ButtonState.Completed

                    val query = DownloadManager.Query()
                    query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
                    val manager =
                        context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val downloadManagerResult: Cursor = manager.query(query)

                    if (downloadManagerResult.moveToFirst()) {
                        if (downloadManagerResult.count > 0) {
                            sendNotification(downloadManagerResult)
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        loadingButton = binding.contentInc.customButton
        downloadOptionsRadioGroup = binding.contentInc.radioList

        setupView()
        createChannel(
            getString(R.string.download_notification_channel_id),
            getString(R.string.download_notification_channel_name)
        )

        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

    private fun setupView() {
        loadingButton.setOnClickListener {
            if (downloadOptionsRadioGroup.isOptionSelected()) {
                loadingButton.state = ButtonState.Loading
                downloadSelectedRepository()
                Toast.makeText(this, "File is downloading", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, getString(R.string.option_select_warning), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Time for breakfast"

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun download(titleTextId: Int, url: String) {
        loadingButton.state = ButtonState.Clicked

        val request: DownloadManager.Request = if (VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(titleTextId))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
        } else {
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(titleTextId))
                .setDescription(getString(R.string.app_description))
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
        }

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        requestDownloadID = downloadManager.enqueue(request)
    }

    private fun sendNotification(cursor: Cursor) {
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendNotification(
            messageBody = getString(R.string.notification_description),
            cursor = cursor,
            applicationContext = applicationContext
        )
    }

    private fun downloadSelectedRepository() {
        when (binding.contentInc.radioList.checkedRadioButtonId) {
            R.id.radio_glide -> download(R.string.glide, GLIDE_URL)
            R.id.radio_loadapp -> download(R.string.loadapp, LOAD_APP_URL)
            R.id.radio_retrofit -> download(R.string.retrofit, RETROFIT_URL)
        }
    }

    companion object {
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/master.zip"

        private const val CHANNEL_ID = "loadapp_channel_id"

        private const val CHANNEL_NAME = "loadapp_download"
    }
}