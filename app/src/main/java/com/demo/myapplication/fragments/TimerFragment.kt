package com.demo.myapplication.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.demo.myapplication.base.BaseFragment
import com.demo.myapplication.databinding.FragmentTimerBinding
import java.util.Locale

class TimerFragment : BaseFragment<FragmentTimerBinding>() {

    private var countDownTimer: CountDownTimer? = null
    private var isTimerRunning = false
    private var timeLeftInMillis: Long = 0

    companion object {
        @JvmStatic
        fun newInstance(): TimerFragment {
            val fragment = TimerFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getBindingView(): FragmentTimerBinding {
        return FragmentTimerBinding.inflate(layoutInflater)
    }

    override fun initData() {

        // Initially check if the start button should be visible or not
        updateStartButtonVisibility()

        binding.btnStartTimer.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        binding.apply {
            binding.timePickerHours.setOnValueChangedListener { _, _, _ ->
                updateStartButtonVisibility()
            }
            binding.timePickerMinutes.setOnValueChangedListener { _, _, _ ->
                updateStartButtonVisibility()
            }
            binding.timePickerSeconds.setOnValueChangedListener { _, _, _ ->
                updateStartButtonVisibility()
            }
        }
    }

    private fun startTimer() {

        // If the timer is not running, initialize the countdown duration from the pickers
        if (timeLeftInMillis == 0L) {
            val hours = binding.timePickerHours.value
            val minutes = binding.timePickerMinutes.value
            val seconds = binding.timePickerSeconds.value

            val totalTimeInMillis = (hours * 3600 + minutes * 60 + seconds) * 1000L
            timeLeftInMillis = totalTimeInMillis
        }

        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountdownText()
            }

            override fun onFinish() {
                isTimerRunning = false
                binding.btnStartTimer.text = "Start Timer"
                binding.btnStartTimer.visibility = View.GONE
                resetNumberPickers()
                updateStartButtonVisibility()
                timeLeftInMillis = 0
            }

        }.start()

        isTimerRunning = true
        binding.btnStartTimer.text = "Pause Timer"
        resetNumberPickers()
        binding.btnStartTimer.visibility = View.VISIBLE
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        binding.btnStartTimer.text = "Start Timer"
        binding.btnStartTimer.visibility = View.VISIBLE
    }

    private fun updateCountdownText() {
        val hours = (timeLeftInMillis / 1000) / 3600
        val minutes = ((timeLeftInMillis / 1000) % 3600) / 60
        val seconds = (timeLeftInMillis / 1000) % 60

        val timeFormatted =
            String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
        binding.tvCountdown.text = timeFormatted
    }

    private fun isTimerZero(): Boolean {
        return binding.timePickerHours.value == 0 && binding.timePickerMinutes.value == 0 && binding.timePickerSeconds.value == 0
    }

    private fun updateStartButtonVisibility() {
        binding.btnStartTimer.visibility =
            if (isTimerZero() && !isTimerRunning) View.GONE else View.VISIBLE
    }

    private fun resetNumberPickers() {
        binding.timePickerHours.value = 0
        binding.timePickerMinutes.value = 0
        binding.timePickerSeconds.value = 0
    }

}