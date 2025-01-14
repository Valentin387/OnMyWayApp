package com.valentinConTilde.onmywayapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import android.provider.Settings

class CustomPermissionHandler (private val context: Context) {
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }

    /*
    The PERMISSION_REQUEST_CODE is simply an arbitrary integer used as a request code to
     identify permission requests in Android. It acts as an identifier so that when the
     system calls onRequestPermissionsResult, your app can know which specific permission
      request is being responded to.
      */

    fun checkAndRequestPermissions(activity: Activity) : Boolean{
        val permissionsNeeded = mutableListOf<String>()

        // Check for each permission whether it's granted
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(android.Manifest.permission.INTERNET)
        }
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(android.Manifest.permission.ACCESS_NETWORK_STATE)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(android.Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }

        // If we have permissions to request, request them
        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionsNeeded.toTypedArray(), PERMISSION_REQUEST_CODE)
            return false
        }
        return true
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        activity: Activity
    ) {

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                val deniedPermissions = mutableListOf<String>()
                var permanentlyDeniedPermissions = mutableListOf<String>()

                // Check if any permissions were denied or permanently denied (Don't ask again)
                for (i in permissions.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])) {
                            // Permission denied with "Don't ask again" selected
                            permanentlyDeniedPermissions.add(permissions[i])
                        } else {
                            // Permission just denied, not permanently
                            deniedPermissions.add(permissions[i])
                        }
                    }
                }

                if (permanentlyDeniedPermissions.isNotEmpty()) {
                    // Handle the case when the user denied with "Don't ask again"
                    showPermanentlyDeniedDialog(permanentlyDeniedPermissions, activity)
                } else if (deniedPermissions.isNotEmpty()) {
                    // Handle regular denial, show rationale dialog, etc.
                    showPermissionDeniedDialog(deniedPermissions, activity)
                } else {
                    // All permissions were granted
                    // Continue with your functionality that requires the permissions
                }
            }
        }
    }

    //I need a function that receives a string with the permission name and returns a more
    //user-friendly message to show in the dialog.
    private fun getPermissionFriendlyName(permission: String): String {
        return when (permission) {
            android.Manifest.permission.ACCESS_FINE_LOCATION -> "permission_ACCESS_FINE_LOCATION"
            android.Manifest.permission.ACCESS_COARSE_LOCATION -> "permission_ACCESS_COARSE_LOCATION"
            android.Manifest.permission.INTERNET -> "permission_INTERNET"
            android.Manifest.permission.ACCESS_NETWORK_STATE -> "permission_ACCESS_NETWORK_STATE"
            android.Manifest.permission.POST_NOTIFICATIONS -> "permission_POST_NOTIFICATIONS"
            android.Manifest.permission.ACTIVITY_RECOGNITION -> "permission_ACTIVITY_RECOGNITION"
            else -> permission
        }
    }

    fun showPermissionDeniedDialog(deniedPermissions: List<String>, activity: Activity) {
        val message = "permission_request_message" +
                deniedPermissions.joinToString(", ") { getPermissionFriendlyName(it) }

        AlertDialog.Builder(activity)
            .setTitle("permission_denied_message")
            .setMessage(message)
            .setPositiveButton("permission_GRANT_AGAIN_button") { dialog, _ ->
                checkAndRequestPermissions(activity)  // Retry permission request
                dialog.dismiss()
            }
            .setNegativeButton("permission_CANCEL_button") { dialog, _ ->
                dialog.dismiss()  // Handle the case when the user denies the permissions again
            }
            .show()
    }

    private fun showPermanentlyDeniedDialog(permanentlyDeniedPermissions: List<String>, activity: Activity) {
        val message = "permission_permanently_denied_message" +
        permanentlyDeniedPermissions.joinToString(", ") { getPermissionFriendlyName(it) }


        AlertDialog.Builder(activity)
            .setTitle("permission_Permanently_Denied_Title")
            .setMessage(message)
            .setPositiveButton("permission_go_to_settings_button") { dialog, _ ->
                // Open the app's settings so the user can manually enable the permissions
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", activity.packageName, null)
                }
                context.startActivity(intent)
                dialog.dismiss()
            }
            .setNegativeButton("permission_CANCEL_button") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}