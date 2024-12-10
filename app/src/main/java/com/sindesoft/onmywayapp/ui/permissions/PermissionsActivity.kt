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
import android.widget.CheckBox
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
    private val permissionCheckboxMap = mutableMapOf<CheckBox, String>()

    companion object {
        const val REQUEST_CODE_PERMISSIONS = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        binding = ActivityPermissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle the "NEXT" button click
        binding.buttonSend.setOnClickListener {
            if(!allPermissionsGranted()) {
                AlertDialog.Builder(this)
                    .setTitle("Permissions Needed")
                    .setMessage("The app needs the following permissions to work correctly: $permissionsNeeded")
                    .setPositiveButton("OK"){ _, _ ->
                        binding.permissionNotifications.isChecked = false
                        binding.permissionLocation.isChecked = false
                        binding.permissionBattery.isChecked = false
                    }
                    .show()
            }else{
                Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        Log.d("PermissionsActivity", "onResume")

    }

    private fun allPermissionsGranted() : Boolean {
        val permissions = mutableListOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.INTERNET
        )

        // Add POST_NOTIFICATIONS permission for devices running Tiramisu or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        permissions.forEach { permission ->
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                Log.d("PermissionsActivity", "Permission $permission needed")
                permissionsNeeded.add(permission)
            }
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(),
                REQUEST_CODE_PERMISSIONS
            )
            return false
        }else{
            return true
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("PermissionsActivity", "onStart")

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
            updateSendButtonState()
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
            updateSendButtonState()
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
            updateSendButtonState()
        }
    }

    private fun isBatteryUnconstrained(): Boolean {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(packageName)
    }

    private fun showBatteryOptimizationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Battery Optimization")
            .setMessage("For the app to work correctly, " +
                    "we need to disable battery optimization. " +
                    "Click 'Open Settings' then click on 'Battery'." +
                    "Please disable battery optimization." +
                    "Go back to the app and click 'NEXT'."
            )
            .setPositiveButton("Open Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel"){ _, _ ->
                binding.permissionBattery.isChecked = false
            }
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
                    "Click 'Open Settings' then click 'Permissions' then 'Location' " +
                    "Please enable 'Allow all the time' and 'Use Precise Location' in the settings." +
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

    private fun updateSendButtonState() {
        // List of all checkboxes
        val checkBoxes = listOf(
            binding.permissionLocation,
            binding.permissionNotifications,
            binding.permissionBattery
        )


        // Enable the button only if all checkboxes are checked
        binding.buttonSend.isEnabled = checkBoxes.all { it.isChecked }


        // Enable the button only if all checkboxes are checked
        //binding.buttonSend.isEnabled = checkBoxes.all { it.isChecked }
    }



}