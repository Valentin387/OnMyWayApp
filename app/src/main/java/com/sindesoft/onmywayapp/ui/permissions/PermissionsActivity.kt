package com.sindesoft.onmywayapp.ui.permissions

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

        //set up CheckBoxes listeners
        Log.d("PermissionsActivity", "onCreate")
        binding.permissionNotifications.setOnCheckedChangeListener { _, isChecked ->
            Log.d("PermissionsActivity", "Notifications")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Log.d("PermissionsActivity", "TIRAMISU")
                handlePermission(
                    isChecked,
                    android.Manifest.permission.POST_NOTIFICATIONS,
                )
            }else{
                Log.d("PermissionsActivity", "PRE-TIRAMISU")
                handlePermission(
                    isChecked,
                    android.Manifest.permission.INTERNET, //Default permission, automatically granted
                )
            }
        }

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

    private fun handlePermission(isChecked: Boolean, vararg permissions: String){
        if (isChecked) {
            permissions.forEach { permission ->
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("PermissionsActivity", "Permission $permission needed")
                    permissionsNeeded.add(permission)
                }
            }
        } else {
            permissionsNeeded.removeAll(permissions.toList())
        }
    }



}