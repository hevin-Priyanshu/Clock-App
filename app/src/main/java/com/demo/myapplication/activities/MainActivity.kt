package com.demo.myapplication.activities

import android.Manifest
import android.content.Intent
import android.os.Build
import android.view.KeyEvent
import androidx.viewpager2.widget.ViewPager2
import com.demo.myapplication.R
import com.demo.myapplication.adapters.ViewPager2Adapter
import com.demo.myapplication.base.BaseActivity
import com.demo.myapplication.databinding.ActivityMainBinding
import com.demo.myapplication.interfaces.PermissionCallback
import com.demo.myapplication.utilities.CommonFunctions.PERMISSION_POST_NOTIFICATIONS
import com.demo.myapplication.utilities.CommonFunctions.PERMISSION_REQUEST_CODE
import com.demo.myapplication.utilities.CommonFunctions.REQUEST_APP_SETTINGS
import com.demo.myapplication.utilities.CommonFunctions.getSharedPref
import com.demo.myapplication.utilities.CommonFunctions.hasPermission
import com.demo.myapplication.utilities.CommonFunctions.showAppSettings
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private var currentFragment: Int = 0
    private lateinit var viewPager2Adapter: ViewPager2Adapter
    private var rationaleDialog: androidx.appcompat.app.AlertDialog? = null
    private var backButtonEnabled = false // Flag to control back press behavior

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            /* Here, if the user navigates to the settings and denies the permission, then repeatedly show the settings dialog, else cancel it.*/
            REQUEST_APP_SETTINGS -> {
                if (hasPermission(PERMISSION_POST_NOTIFICATIONS)) {
                    rationaleDialog?.cancel()
                } else {
                    showRationaleOrOpenSettingsDialog()
                }
            }
        }
    }

    override fun initData() {
        initPermission()
        setUp()
    }

    private fun initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkAndRequestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                object : PermissionCallback {
                    override fun onPermissionsGranted() {
                        setUp()
                    }

                    override fun onPermissionDeniedFirstTime() {
                        showRationaleDialog()
                    }

                    override fun onPermissionsDenied() {
                        showRationaleOrOpenSettingsDialog()
                    }
                })
        }
    }

    private fun setUp() {
        backButtonEnabled = true
        setupViewPager()
        setUpBottomNavigation()
        selectPreviousVisitFragment()
    }

    private fun selectPreviousVisitFragment() {

        // Retrieve the last selected fragment from SharedPreferences
        val lastFragment = getSharedPref.setOrGetLastFragment
        // Set the bottom navigation selected item to match the last fragment
        when (lastFragment) {
            0 -> selectBottomNavItemId(R.id.alarmItem)
            1 -> selectBottomNavItemId(R.id.clockItem)
            2 -> selectBottomNavItemId(R.id.timerItem)
            3 -> selectBottomNavItemId(R.id.stopwatchItem)
        }
    }

    private fun setupViewPager() {

        binding.apply {

            // Disable user swiping
            viewPagerMain.isUserInputEnabled = false
            viewPager2Adapter = ViewPager2Adapter(this@MainActivity)
            viewPagerMain.offscreenPageLimit = 4
            viewPagerMain.adapter = viewPager2Adapter

            viewPagerMain.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                override fun onPageScrolled(
                    position: Int, positionOffset: Float, positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    currentFragment = position
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    currentFragment = position

                    // Update the BottomNavigationView to reflect the current page
                    when (position) {
                        0 -> {
                            selectBottomNavItemId(R.id.alarmItem)
                            mainToolbar.title = getString(R.string.alarm)
                        }

                        1 -> {
                            selectBottomNavItemId(R.id.clockItem)
                            mainToolbar.title = getString(R.string.clock)
                        }

                        2 -> {
                            selectBottomNavItemId(R.id.timerItem)
                            mainToolbar.title = getString(R.string.timer)
                        }

                        3 -> {
                            selectBottomNavItemId(R.id.stopwatchItem)
                            mainToolbar.title = getString(R.string.stopwatch)
                        }
                    }

//                 Save the currently selected fragment to SharedPreferences
                    getSharedPref.setOrGetLastFragment = position
                }
            })

        }
    }

    private fun setUpBottomNavigation() {
        binding.bottomNavigationDefault.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.alarmItem -> setViewpagerCurrItem(0)
                R.id.clockItem -> setViewpagerCurrItem(1)
                R.id.timerItem -> setViewpagerCurrItem(2)
                R.id.stopwatchItem -> setViewpagerCurrItem(3)
            }
            true
        }
    }

    private fun selectBottomNavItemId(item: Int) {
        binding.bottomNavigationDefault.selectedItemId = item
    }

    private fun setViewpagerCurrItem(currItem: Int) {
        binding.viewPagerMain.setCurrentItem(currItem, false)
    }

    private fun showRationaleDialog() {
        showDialog(title = getString(R.string.permission_required),
            message = getString(R.string.permission_required_text),
            positiveButtonText = getString(R.string.ok),
            positiveButtonAction = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissions(
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS), PERMISSION_REQUEST_CODE
                    )
                }
            },
            negativeButtonText = getString(R.string.cancel),
            negativeButtonAction = {
                // Custom action for the negative button if needed, currently dismissing the dialog
            })
    }

    private fun showRationaleOrOpenSettingsDialog() {
        showDialog(title = getString(R.string.permission_required),
            message = getString(R.string.permission_required_text),
            positiveButtonText = getString(R.string.go_to_settings),
            positiveButtonAction = {
                showAppSettings(this)
            },
            negativeButtonText = getString(R.string.cancel),
            negativeButtonAction = {
                finish()
            })
    }

    private fun showDialog(
        title: String,
        message: String,
        positiveButtonText: String,
        positiveButtonAction: () -> Unit,
        negativeButtonText: String,
        negativeButtonAction: () -> Unit
    ) {
        backButtonEnabled = false // Disable back press while showing rationale

        val builder = MaterialAlertDialogBuilder(this).setTitle(title).setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                dialog.dismiss()
                positiveButtonAction()
            }.setNegativeButton(negativeButtonText) { dialog, _ ->
                dialog.dismiss()
                negativeButtonAction()
            }

        rationaleDialog?.setOnKeyListener { _, keyCode, event ->
            keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP
        }

        rationaleDialog = builder.create().apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }
        rationaleDialog?.show()
    }

}