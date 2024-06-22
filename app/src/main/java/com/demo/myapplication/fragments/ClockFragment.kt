package com.demo.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.myapplication.activities.SelectTimeZoneActivity
import com.demo.myapplication.adapters.AddNewTimeZoneAdapter
import com.demo.myapplication.base.BaseFragment
import com.demo.myapplication.databinding.FragmentClockBinding
import com.demo.myapplication.models.TimeZoneModel
import com.demo.myapplication.viewmodel.activitiesViewModel.MainActivityViewModel
import com.demo.myapplication.viewmodel.factory.ViewModelFactory

class ClockFragment : BaseFragment<FragmentClockBinding>() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels { ViewModelFactory }
    private lateinit var adapter: AddNewTimeZoneAdapter

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

        val list = mainActivityViewModel.getAllTimeZoneList
        setupRecyclerView(list)
//        val getList: List<TimeZoneModel> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            getAllTimezone()
//        } else {
//            emptyList()
//        }

        binding.floatingActionButton.setOnClickListener {
            Intent(mContext, SelectTimeZoneActivity::class.java).apply {
                startActivity(this)
            }
        }
    }


    private fun setupRecyclerView(list: List<TimeZoneModel>) {
        binding.clockRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = AddNewTimeZoneAdapter()
        binding.clockRecyclerView.adapter = adapter
        adapter.submitList(list)
    }

}