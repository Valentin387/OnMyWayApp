package com.sindesoft.onmywayapp.ui.permissions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.ui.AppBarConfiguration
import com.sindesoft.onmywayapp.databinding.ActivityMainBinding
import com.sindesoft.onmywayapp.databinding.ActivityPermissionsBinding


class PermissionsActivity: AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityPermissionsBinding
    private val permissionsNeeded = mutableListOf<String>()

    companion object {
        const val REQUEST_CODE_PERMISSIONS = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        binding = ActivityPermissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Enable the "NEXT" button only if all permissions are granted
        val checkBoxes = listOf(
            binding.permissionNotifications,
        )

        checkBoxes.forEach { checkBox ->
            checkBox.setOnCheckedChangeListener { _, _ ->
                binding.buttonSend.isEnabled = checkBoxes.all { it.isChecked }
            }
        }

        // Handle the "NEXT" button click
        binding.buttonSend.setOnClickListener {
            if (permissionsNeeded.isNotEmpty()) {
                // Ask for permissions if any are still needed
                ActivityCompat.requestPermissions(
                    this,
                    permissionsNeeded.toTypedArray(),
                    REQUEST_CODE_PERMISSIONS
                )
            } else {
                Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun onResume() {
        super.onResume()
        Log.d("PermissionsActivity", "onResume")

        binding.permissionNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                handlePermission(
                    isChecked,
                    android.Manifest.permission.POST_NOTIFICATIONS,
                )
            }else{
                handlePermission(
                    isChecked,
                    android.Manifest.permission.INTERNET, //Default permission, automatically granted
                )
            }
        }

        binding.permissionLocation.setOnCheckedChangeListener { _, isChecked ->
            handlePermission(
                isChecked,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            // I need to check if the user granted the FINE location permission and
            // also prompt them to change the authorisation level from "While the app is in use" to "Always"
            handleLocationPermission()
        }

        binding.permissionBattery.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager

                if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                    // The app is constrained, show the dialog
                    showBatteryOptimizationDialog()
                } else {
                    Toast.makeText(this, "Battery optimization already disabled for this app.", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    private fun showBatteryOptimizationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Battery Optimization")
            .setMessage("For the app to work correctly, " +
                    "we need to disable battery optimization. " +
                    "Click 'Open Settings' search OnMyWayApp." +
                    "Please disable battery optimization in the settings." +
                    "Go back to the app and click 'NEXT'."
            )
            .setPositiveButton("Open Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun handlePermission(isChecked: Boolean, vararg permissions: String){
        if (isChecked) {
            permissions.forEach { permission ->
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("PermissionsActivity", "Permission $permission needed")
                    permissionsNeeded.add(permission)
                    ActivityCompat.requestPermissions(
                        this,
                        permissionsNeeded.toTypedArray(),
                        REQUEST_CODE_PERMISSIONS
                    )
                }
            }
        } else {
            permissionsNeeded.removeAll(permissions.toList())
        }
    }

    private fun handleLocationPermission(){
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            Log.d("PermissionsActivity", "Permission ACCESS_FINE_LOCATION needed")
            //Request FINE location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_PERMISSIONS
            )
        }else{
            Log.d("PermissionsActivity", "Permission ACCESS_FINE_LOCATION granted")
            Toast.makeText(this, "FINE location granted!", Toast.LENGTH_SHORT).show()
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                // Prompt the user to enable "Always" location
                showAlwaysLocationPrompt()
            } else {
                Log.d("PermissionsActivity", "Location permission is set to Always")
                Toast.makeText(this, "Location permission is set to Always!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun showAlwaysLocationPrompt() {
        AlertDialog.Builder(this)
            .setTitle("Change Location Access")
            .setMessage("For the app to work correctly, " +
                    "we need access to your location all the time. " +
                    "Click 'Open Settings' search OnMyWayApp." +
                    "Please enable 'Always Allow' in the settings." +
                    "Go back to the app and click 'NEXT'."
            )
            .setPositiveButton("Open Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }




}