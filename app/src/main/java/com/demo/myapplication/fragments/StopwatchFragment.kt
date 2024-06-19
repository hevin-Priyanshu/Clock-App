package com.demo.myapplication.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.myapplication.R
import com.demo.myapplication.adapters.LapDetailsAdapter
import com.demo.myapplication.base.BaseFragment
import com.demo.myapplication.databinding.FragmentStopWatchBinding
import com.demo.myapplication.extensions.gone
import com.demo.myapplication.extensions.visible
import com.demo.myapplication.helper.NotificationHelper
import com.demo.myapplication.models.LapDetails

class StopwatchFragment : BaseFragment<FragmentStopWatchBinding>() {

    private val notificationHelper by lazy { NotificationHelper(requireContext()) }
    private lateinit var lapDetailsAdapter: LapDetailsAdapter
    private var lapDetailsList = mutableListOf<LapDetails>()
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private var lapCount: Int = 1
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timer: CountDownTimer
    private var isTimerRunning = false
    private var lastLapTime: Long = 0

    companion object {
        @JvmStatic
        fun newInstance(): StopwatchFragment {
            val fragment = StopwatchFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getBindingView(): FragmentStopWatchBinding {
        return FragmentStopWatchBinding.inflate(layoutInflater)
    }

    private val updateNotificationRunnable = object : Runnable {
        override fun run() {
            if (isTimerRunning) {
                elapsedTime = SystemClock.elapsedRealtime() - startTime
                updateTimerText(elapsedTime)
                handler.postDelayed(this, 10)
            }
        }
    }

    override fun initData() {

        setupRecyclerView()
        binding.buttonStartStop.setOnClickListener {
            if (isTimerRunning) {
                pauseChronometer()
            } else {
                startChronometer()
            }
        }

        binding.buttonReset.setOnClickListener {
            resetChronometer()
        }

        binding.buttonLab.setOnClickListener {
            createLapDetail()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        lapDetailsAdapter = LapDetailsAdapter()
        binding.recyclerView.adapter = lapDetailsAdapter
    }

    private fun createLapDetail() {
        if (isTimerRunning) {
            val currentElapsedTime = SystemClock.elapsedRealtime() - startTime
            val lapTime = calculateLapTime(currentElapsedTime)
            val overallTime = elapsedTimeToString(currentElapsedTime)
            val lapDetails = LapDetails(lapCount++, lapTime, overallTime)
            lapDetailsList.add(0, lapDetails)
            lapDetailsAdapter.submitList(lapDetailsList.toList())
            lapDetailsAdapter.notifyDataSetChanged()
            binding.recyclerView.scrollToPosition(0)
            lastLapTime = currentElapsedTime
        }
    }

    private fun calculateLapTime(currentElapsedTime: Long): String {
        val lapTimeMillis = currentElapsedTime - lastLapTime
        val minutes = (lapTimeMillis / 60000).toInt()
        val seconds = (lapTimeMillis % 60000 / 1000).toInt()
        val milliseconds = (lapTimeMillis % 1000 / 10).toInt()
        return "%02d:%02d.%02d".format(minutes, seconds, milliseconds)
    }


    private fun elapsedTimeToString(elapsedTime: Long): String {
        val minutes = (elapsedTime / 60000).toInt()
        val seconds = (elapsedTime % 60000 / 1000).toInt()
        val milliseconds = (elapsedTime % 1000 / 10).toInt()
        return "%02d:%02d.%02d".format(minutes, seconds, milliseconds)
    }


//    private fun startTimer() {
//        timer = object : CountDownTimer(Long.MAX_VALUE, 1) {
//            override fun onTick(millisUntilFinished: Long) {
//                val delta: Long = SystemClock.elapsedRealtime() - binding.chronometer.base
//                val h = (delta / 1000 / 3600).toInt()
//                val m = (delta / 1000 / 60 % 60).toInt()
//                val s = (delta / 1000 % 60).toInt()
//                val millisecond = (delta % 1000 / 10)
//                val customText = String.format("%02d:%02d.%02d", m, s, millisecond)
//                binding.chronometer.text = customText
//            }
//
//            override fun onFinish() {
//                // Restart the timer if it finishes (should not happen)
//                startTimer()
//            }
//        }
//        timer.start()
//    }


    private fun startChronometer() {
        isTimerRunning = true
        startTime = SystemClock.elapsedRealtime() - elapsedTime
        binding.apply {
            buttonLab.visible()
            buttonReset.visible()
            buttonStartStop.setImageResource(R.drawable.ic_stop)
        }
        notificationHelper.showNotification(startTime)
        handler.post(updateNotificationRunnable)
    }


//    private fun startChronometer() {
//
//        isTimerRunning = true
//        binding.apply {
////            chronometer.base = SystemClock.elapsedRealtime() - offset
//            chronometer.start()
////            startTimer()
//
////            chronometer.format = "Time %s"
//            buttonStartStop.text = "Stop"
////            val elapsedMillis = SystemClock.elapsedRealtime() - binding.chronometer.base
//
//
////            var seconds: Long
////            chronometer.onChronometerTickListener = Chronometer.OnChronometerTickListener {
////                seconds = ((SystemClock.elapsedRealtime() - chronometer.base) * 1000)
////                val strTime = (seconds).toString()
//////                onChronometerTickHandler()
//////                lblTime.setText(strTime)
////            }
//
//            notificationHelper.showNotification(chronometer.base)
//        }
////        handler.post(updateNotificationRunnable)
//    }

    private fun updateTimerText(elapsedTime: Long) {
        val minutes = (elapsedTime / 60000).toInt()
        val seconds = (elapsedTime % 60000 / 1000).toInt()
        val milliseconds = (elapsedTime % 1000 / 10).toInt()
        val customText = String.format("%02d:%02d.%02d", minutes, seconds, milliseconds)
        binding.stopwatchTime.text = customText
    }

    private fun pauseChronometer() {
        if (isTimerRunning) {
            isTimerRunning = false
            elapsedTime = SystemClock.elapsedRealtime() - startTime

            binding.apply {
                buttonLab.gone()
                buttonStartStop.setImageResource(R.drawable.ic_start)
            }
            handler.removeCallbacks(updateNotificationRunnable)
            notificationHelper.cancelNotification()
        }
    }

//    private val updateNotificationRunnable = object : Runnable {
//        override fun run() {
//            val delta: Long = SystemClock.elapsedRealtime() - offset
//            val h = (delta / 1000 / 3600).toInt()
//            val m = (delta / 1000 / 60 % 60).toInt()
//            val s = (delta / 1000 % 60).toInt()
//            val millisec = (delta % 1000).toInt()
//            val customText = String.format("%02d:%02d.%03d", m, s, millisec)
//            binding.chronometer.text = customText
//            handler.postDelayed(this, 1)
//        }
//    }


    private fun resetChronometer() {
        isTimerRunning = false
        binding.apply {
            buttonStartStop.setImageResource(R.drawable.ic_start)
            buttonLab.gone()
            buttonReset.gone()
        }
        elapsedTime = 0
        updateTimerText(elapsedTime)
        lapCount = 1
        startTime = 0
        lastLapTime = 0
        lapDetailsList.clear()
        lapDetailsAdapter.submitList(lapDetailsList)
        notificationHelper.cancelNotification()
        handler.removeCallbacks(updateNotificationRunnable)
    }

}