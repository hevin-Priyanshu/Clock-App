package com.demo.myapplication.activities

import android.os.Build
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.myapplication.adapters.TimeZoneAdapter
import com.demo.myapplication.base.BaseActivity
import com.demo.myapplication.databinding.ActivitySelectTimeZoneBinding
import com.demo.myapplication.extensions.gone
import com.demo.myapplication.extensions.visible
import com.demo.myapplication.models.TimeZoneModel
import com.demo.myapplication.utilities.CommonFunctions.getAllTimezone
import com.demo.myapplication.viewmodel.activitiesViewModel.MainActivityViewModel
import com.demo.myapplication.viewmodel.factory.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectTimeZoneActivity : BaseActivity<ActivitySelectTimeZoneBinding>() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels { ViewModelFactory }
    private lateinit var timeZoneAdapter: TimeZoneAdapter
    private var selectedArraylist = ArrayList<Int>()
    override fun getViewBinding(): ActivitySelectTimeZoneBinding {
        return ActivitySelectTimeZoneBinding.inflate(layoutInflater)
    }

    override fun initData() {
        binding.progressCircular.visible()
        binding.rvWorldClock.gone()
        setupRecyclerView()
        lifecycleScope.launch(Dispatchers.IO) {
            val getList: List<TimeZoneModel> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getAllTimezone()
            } else {
                emptyList()
            }

            withContext(Dispatchers.Main) {
                timeZoneAdapter.submitList(getList)
                binding.rvWorldClock.visible()
                binding.progressCircular.gone()

            }
        }

        onBack {
            finish()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnSelectTimeZone.setOnClickListener {
            MainScope().launch {
                Log.d("", "initData: $selectedArraylist")
                mainActivityViewModel.addSelectedTimeZones(selectedArraylist)
                finish()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvWorldClock.layoutManager = LinearLayoutManager(this)
        timeZoneAdapter = TimeZoneAdapter(this, selectedArraylist) {
            selectedArraylist = it
        }
        binding.rvWorldClock.adapter = timeZoneAdapter
    }
}