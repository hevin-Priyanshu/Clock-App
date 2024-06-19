package com.demo.myapplication.fragments

import android.os.Bundle
import com.demo.myapplication.base.BaseFragment
import com.demo.myapplication.databinding.FragmentClockBinding

class ClockFragment : BaseFragment<FragmentClockBinding>() {

    companion object {
        @JvmStatic
        fun newInstance(): ClockFragment {
            val fragment = ClockFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getBindingView(): FragmentClockBinding {
        return FragmentClockBinding.inflate(layoutInflater)
    }

    override fun initData() {

    }


}