package br.com.bernardino.loadapp

import android.app.DownloadManager
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import br.com.bernardino.loadapp.databinding.ActivityDetailBinding
import br.com.bernardino.loadapp.extensions.BUNDLE_FILE_STATUS
import br.com.bernardino.loadapp.extensions.cancelNotifications

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)

        var bundleFileStatus = intent.extras?.getBundle(BUNDLE_FILE_STATUS)
        var status = bundleFileStatus?.getInt(FILE_STATUS)

        binding.layoutDetail.tvFilename.text = bundleFileStatus?.getString(FILE_NAME)

        if (status == DownloadManager.STATUS_SUCCESSFUL) {
            binding.layoutDetail.tvStatus.setTextColor(getColor(R.color.green))
            binding.layoutDetail.tvStatus.text = getString(R.string.success)
        } else {
            binding.layoutDetail.tvStatus.setTextColor(getColor(R.color.red))
            binding.layoutDetail.tvStatus.text = getString(R.string.fail)
        }

        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)

        cancelNotifications()
        setUpMotionLayoutTransitionEndListener()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun cancelNotifications() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancelNotifications()
    }

    private fun setUpMotionLayoutTransitionEndListener() {
        val motionLayout = binding.layoutDetail.contentDetailMotionLayout
        motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout, i: Int, i1: Int) {}

            override fun onTransitionChange(motionLayout: MotionLayout, i: Int, i1: Int, v: Float) {

            }

            override fun onTransitionCompleted(motionLayout: MotionLayout, i: Int) {
                finish()
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })
    }

    companion object {
        const val FILE_NAME = "FILE_NAME"
        const val FILE_STATUS = "FILE_STATUS"
    }

}
