package com.demo.myapplication.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import android.os.SystemClock
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.demo.myapplication.R
import com.demo.myapplication.utilities.CommonFunctions.notificationManager


class NotificationHelper(private val context: Context) {

    private val notificationManager = context.notificationManager
    private lateinit var timer: CountDownTimer
    private var notificationLayout: RemoteViews? = null

    companion object {
        const val CHANNEL_ID = "stopwatch_channel"
        const val NOTIFICATION_ID = 100
    }

    init {
        createNotificationChannel()
    }


    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Stopwatch Channel"
            val descriptionText = "Channel for stopwatch notifications"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(baseTime: Long) {

        notificationLayout =
            RemoteViews(context.packageName, R.layout.notification_chronometer_layout)
//        val currentTime = SystemClock.elapsedRealtime()
//        val elapsedMillis = currentTime - baseTime
//
//        // Calculate hours, minutes, seconds, and milliseconds
//        val hours = (elapsedMillis / (1000 * 60 * 60)).toInt()
//        val minutes = ((elapsedMillis % (1000 * 60 * 60)) / (1000 * 60)).toInt()
//        val seconds = ((elapsedMillis % (1000 * 60)) / 1000).toInt()
//        val milliseconds = (elapsedMillis % 1000).toInt()
//
//        // Format the time into a string
//        val timeText = String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds)

//        startTimer(baseTime)
        // Update the Chronometer base time and start it
        val currentTime = SystemClock.elapsedRealtime()
        notificationLayout?.setChronometer(
            R.id.chronometer_notification, baseTime, null, true
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            val builder = Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setCustomContentView(notificationLayout).setPriority(Notification.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_ALARM).setOngoing(true).setChannelId(CHANNEL_ID)
                .build()

            notificationManager.notify(NOTIFICATION_ID, builder)
        } else {

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground).setContent(notificationLayout)
                .setPriority(Notification.PRIORITY_HIGH).setCategory(Notification.CATEGORY_ALARM)
                .setOngoing(true).setSound(null).setChannelId(CHANNEL_ID).build()

            notificationManager.notify(NOTIFICATION_ID, builder)
        }
    }

    private fun startTimer(baseTime: Long) {
        timer = object : CountDownTimer(Long.MAX_VALUE, 1) {
            override fun onTick(millisUntilFinished: Long) {
                val delta: Long = SystemClock.elapsedRealtime() - baseTime
                val h = (delta / 1000 / 3600).toInt()
                val m = (delta / 1000 / 60 % 60).toInt()
                val s = (delta / 1000 % 60).toInt()
                val millisecond = (delta % 1000 / 10)
                val customText = String.format("%02d:%02d:%02d.%02d", h, m, s, millisecond)

                // Update the Chronometer text with the formatted time
                notificationLayout?.setTextViewText(R.id.chronometer_notification, customText)
            }

            override fun onFinish() {
                // Restart the timer if it finishes (should not happen)
                startTimer(baseTime)
            }
        }
        timer.start()
    }


    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
