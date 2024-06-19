package com.demo.myapplication.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.demo.myapplication.fragments.AlarmFragment
import com.demo.myapplication.fragments.ClockFragment
import com.demo.myapplication.fragments.StopwatchFragment
import com.demo.myapplication.fragments.TimerFragment


class ViewPager2Adapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount() = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AlarmFragment.newInstance()
            1 -> ClockFragment.newInstance()
            2 -> TimerFragment.newInstance()
            3 -> StopwatchFragment.newInstance()
            else -> AlarmFragment.newInstance()
        }
    }

}