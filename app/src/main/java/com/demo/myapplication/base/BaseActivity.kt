package com.demo.myapplication.base

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.demo.myapplication.helper.PermissionHelper
import com.demo.myapplication.interfaces.PermissionCallback

abstract class BaseActivity<B : ViewBinding> : AppCompatActivity() {

    lateinit var binding: B
    private lateinit var permissionHelper: PermissionHelper
    abstract fun getViewBinding(): B
    abstract fun initData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = getViewBinding()
        setContentView(binding.root)
        permissionHelper = PermissionHelper(this)
        initData()
    }

    fun checkAndRequestPermissions(permissions: Array<String>, callback: PermissionCallback) {
        permissionHelper.checkAndRequestPermissions(permissions, callback)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper.handlePermissionsResult(requestCode, permissions, grantResults)
    }

    fun onBack(onBackPressed: () -> Unit) {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed.invoke()
            }
        })
    }

}