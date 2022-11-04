package br.com.bernardino.loadapp.extensions

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.core.app.NotificationCompat
import br.com.bernardino.loadapp.DetailActivity
import br.com.bernardino.loadapp.MainActivity
import br.com.bernardino.loadapp.R

// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

/**
 * Builds and delivers the notification.
 *
 * @param context, activity context.
 */
fun NotificationManager.sendNotification(
    messageBody: String,
    cursor: Cursor,
    applicationContext: Context
) {

    val downloadImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.download_icon
    )

    val bigPictureStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(downloadImage)
        .bigLargeIcon(null)

    val openDetailIntent = Intent(applicationContext, DetailActivity::class.java)
    openDetailIntent.putExtra(BUNDLE_FILE_STATUS, getDetailActivityIntentBundle(cursor))

    val openDetailPendingIntent: PendingIntent = PendingIntent.getActivity(
        applicationContext,
        REQUEST_CODE,
        openDetailIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.download_notification_channel_id)
    )
        .setSmallIcon(R.drawable.download_icon)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(openDetailPendingIntent)
        .setAutoCancel(true)
        .setStyle(bigPictureStyle)
        .setLargeIcon(downloadImage)
        .addAction(
            R.drawable.information_icon,
            applicationContext.getString(R.string.open_detail),
            openDetailPendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)


    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}

@SuppressLint("Range")
private fun getDetailActivityIntentBundle(cursor: Cursor) = Bundle().apply {
    putString(
        DetailActivity.FILE_NAME,
        cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
    )
    putInt(
        DetailActivity.FILE_STATUS,
        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
    )
}

const val BUNDLE_FILE_STATUS = "BUNDLE_STATUS"