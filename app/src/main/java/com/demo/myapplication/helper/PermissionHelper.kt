package com.demo.myapplication.helper

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.demo.myapplication.interfaces.PermissionCallback
import com.demo.myapplication.utilities.CommonFunctions.PERMISSION_REQUEST_CODE

class PermissionHelper(private val activity: Activity) {

    private var callback: PermissionCallback? = null

    fun checkAndRequestPermissions(permissions: Array<String>, callback: PermissionCallback) {
        this.callback = callback
        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missingPermissions.isEmpty()) {
            callback.onPermissionsGranted()
        } else {
            requestPermissions(activity, missingPermissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    fun handlePermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                when {
                    grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED } -> {
                        callback?.onPermissionsGranted()
                    }

                    permissions.any {
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            activity, it
                        )
                    } -> {
                        callback?.onPermissionDeniedFirstTime()
                    }

                    else -> {
                        callback?.onPermissionsDenied()
                    }
                }
            }
        }
        //////////
    }

}