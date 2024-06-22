package com.demo.myapplication.helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.demo.myapplication.R
import com.demo.myapplication.activities.MainActivity
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

    fun showNotification(startTime: Long) {

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

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("isStopwatchRunning", true)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )



        notificationLayout?.setChronometer(
            R.id.notification_chronometer, startTime, null, true
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            val builder =
                NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Stopwatch Running")
                    .setSmallIcon(R.drawable.ic_alarm)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setCustomContentView(notificationLayout).setOngoing(true).setSound(null)
                    .setChannelId(CHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())

            val notification = builder.build()
            notificationManager.notify(NOTIFICATION_ID, notification)

        }
    }

//    private fun startTimer(baseTime: Long) {
//        timer = object : CountDownTimer(Long.MAX_VALUE, 1) {
//            override fun onTick(millisUntilFinished: Long) {
//                val delta: Long = SystemClock.elapsedRealtime() - baseTime
//                val h = (delta / 1000 / 3600).toInt()
//                val m = (delta / 1000 / 60 % 60).toInt()
//                val s = (delta / 1000 % 60).toInt()
//                val millisecond = (delta % 1000 / 10)
//                val customText = String.format("%02d:%02d:%02d.%02d", h, m, s, millisecond)
//
//                // Update the Chronometer text with the formatted time
//                notificationLayout?.setTextViewText(R.id.notification_chronometer, customText)
//            }
//
//            override fun onFinish() {
//                // Restart the timer if it finishes (should not happen)
//                startTimer(baseTime)
//            }
//        }
//        timer.start()
//    }

    private fun elapsedTimeToLong(elapsedTime: Long): Long {
        val minutes = (elapsedTime / 60000).toInt()
        val seconds = (elapsedTime % 60000 / 1000).toInt()
        val milliseconds = (elapsedTime % 1000 / 10).toInt()

        // Convert the time parts to a single Long by concatenating them

        return "%02d%02d%02d".format(minutes, seconds, milliseconds).toLong()
    }


    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
