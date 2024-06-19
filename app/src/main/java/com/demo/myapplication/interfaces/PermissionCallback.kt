package com.demo.myapplication.interfaces

interface PermissionCallback {
    fun onPermissionsGranted()
    fun onPermissionDeniedFirstTime()
    fun onPermissionsDenied()
}