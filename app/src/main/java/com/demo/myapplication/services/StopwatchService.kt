package com.demo.myapplication.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.widget.Chronometer
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.demo.myapplication.R
import com.demo.myapplication.activities.MainActivity
import com.demo.myapplication.events.ResetStopwatchTimerEvent
import com.demo.myapplication.events.TimerRunningEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class StopwatchService : Service() {

    private lateinit var chronometer: Chronometer
    private var isTimerRunning = false
    private val notificationId = 1000
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private lateinit var notificationLayout: RemoteViews
    private lateinit var notificationManager: NotificationManager

    companion object {
        const val ACTION_START = "action_start"
        const val ACTION_STOP = "action_stop"
        const val ACTION_RESET = "action_reset"
        const val ACTION_START_STOP = "action_start_stop"
    }

    override fun onCreate() {
        super.onCreate()
        chronometer = Chronometer(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        EventBus.getDefault().register(this)
        startForeground(notificationId, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> handleStartAction()
            ACTION_STOP -> handleStopAction()
            ACTION_RESET -> handleResetAction()
            ACTION_START_STOP -> handleStartStopAction()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTimerRunningEvent(event: TimerRunningEvent) {
        startTimer()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onResetStopwatchEvent(event: ResetStopwatchTimerEvent) {
        resetStopwatch()
    }

    private fun handleStartAction() {
        startTimer()
    }

    private fun handleStopAction() {
        stopTimer()
    }

    private fun handleResetAction() {
        resetStopwatch()
    }

    private fun handleStartStopAction() {
        if (isTimerRunning) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        if (!isTimerRunning) {
            startTime = SystemClock.elapsedRealtime() - elapsedTime
            chronometer.base = startTime
            chronometer.start()
            isTimerRunning = true
            updateNotification()
        }
    }

    private fun stopTimer() {
        if (isTimerRunning) {
            chronometer.stop()
            elapsedTime = SystemClock.elapsedRealtime() - startTime
            isTimerRunning = false
            updateNotification()
        }
    }

    private fun resetStopwatch() {
        chronometer.stop()
        elapsedTime = 0
        startTime = 0
        isTimerRunning = false
        updateNotification()
        stopForeground(true)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateNotification() {
        val notification = createNotification()
        notificationManager.notify(notificationId, notification)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createNotification(): Notification {
        val channelId = "stopwatch_channel"
        val channelName = "Stopwatch Channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("isStopwatchRunning", true)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val startStopIntent = Intent(this, StopwatchService::class.java).apply {
            action = ACTION_START_STOP
        }

        val startStopPendingIntent = PendingIntent.getService(
            this,
            0,
            startStopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

//        elapsedTime = SystemClock.elapsedRealtime() - startTime
//
//        notificationLayout =
//            RemoteViews(packageName, R.layout.notification_chronometer_layout).apply {
//                setChronometer(
//                    R.id.notification_chronometer,
//                    SystemClock.elapsedRealtime() - elapsedTime,
//                    null,
//                    true
//                )
//            }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Stopwatch Running")
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentIntent(pendingIntent)
            .setChronometerCountDown(true)
            .setUsesChronometer(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSilent(true)
            .addAction(R.drawable.ic_alarm, "Stop", startStopPendingIntent)
            .build()
    }
}


