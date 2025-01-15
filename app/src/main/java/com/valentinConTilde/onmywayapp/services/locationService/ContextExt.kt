package com.valentinConTilde.onmywayapp.services.locationService

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this, //the context
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}
 /*The  hasLocationPermission  function checks if the app has the necessary permissions to access the device’s location.
 The  ContextCompat.checkSelfPermission  function checks if the app has the permission to access the device’s location.
 The  Manifest.permission.ACCESS_COARSE_LOCATION  and  Manifest.permission.ACCESS_FINE_LOCATION  are the permissions that the app needs to access the device’s location.
 The  PackageManager.PERMISSION_GRANTED  is the value that the  ContextCompat.checkSelfPermission  function returns if the app has the permission to access the device’s location.
 The  hasLocationPermission  function returns  true  if the app has the necessary permissions to access the device’s location.
 The  hasLocationPermission  function returns  false  if the app does not have the necessary permissions to access the device’s location.
*/