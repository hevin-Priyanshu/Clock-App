package com.demo.myapplication.fragments

import android.os.Bundle
import com.demo.myapplication.base.BaseFragment
import com.demo.myapplication.databinding.FragmentAlarmBinding

class AlarmFragment : BaseFragment<FragmentAlarmBinding>() {

    companion object {
        @JvmStatic
        fun newInstance(): AlarmFragment {
            val fragment = AlarmFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getBindingView(): FragmentAlarmBinding {
        return FragmentAlarmBinding.inflate(layoutInflater)
    }

    override fun initData() {

    }

}