package com.valentinConTilde.onmywayapp.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.provider.Settings
import android.view.MenuItem
import android.widget.ImageView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.valentinConTilde.onmywayapp.R
import com.valentinConTilde.onmywayapp.data.models.User
import com.valentinConTilde.onmywayapp.databinding.ActivityMainBinding
import com.valentinConTilde.onmywayapp.databinding.NavHeaderMainBinding
import com.valentinConTilde.onmywayapp.ui.auth.LoginActivity
import com.valentinConTilde.onmywayapp.ui.permissions.PermissionsActivity
import com.valentinConTilde.onmywayapp.utils.CustomPermissionHandler
import com.valentinConTilde.onmywayapp.utils.EncryptedPrefsManager

import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.Date

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.widget.Toast
import com.valentinConTilde.onmywayapp.services.locationService.LocationService
import com.valentinConTilde.onmywayapp.utils.MyAlarmReceiver

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity

import com.valentinConTilde.onmywayapp.utils.TransitionReceiver



class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionHandler: CustomPermissionHandler
    private val loggedExceptions = mutableSetOf<String>() // To store unique exceptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the EncryptedPrefsManager
        EncryptedPrefsManager.init(applicationContext)

        // Save the original exception handler
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            saveCrashLog(exception) // Log the error
            defaultHandler?.uncaughtException(thread, exception)  // Pass the exception to the default handler
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_subscriptions, R.id.nav_subscribers
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val preferences = EncryptedPrefsManager.getPreferences()
        //get the user
        val gson = Gson()
        val userJson = preferences.getString("user", null)
        val user = gson.fromJson(userJson, User::class.java)
        Log.d("MainActivity", "User: $user")

        //access the nav_header_main view
        val navHeaderMainBinding = NavHeaderMainBinding.bind(navView.getHeaderView(0))
        val profileImage: ImageView = navHeaderMainBinding.profileImage
        Glide.with(this)
            .load(user.profilePicture) // Load the URL from the user object
            .placeholder(R.mipmap.ic_launcher_round) // Optional: Placeholder image
            .error(R.mipmap.ic_launcher_round) // Optional: Error image
            .into(profileImage)
        navHeaderMainBinding.tvFullName.text = user.familyName + " " + user.givenName
        navHeaderMainBinding.tvEmail.text = user.email
        navHeaderMainBinding.tvAssignedCode.text = user.assignedCode

        //Fine-grained permission handling here
        permissionHandler = CustomPermissionHandler(this)


        //List of transitions to be monitored
        val transitions = listOf(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_FOOT)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_FOOT)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build(),
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build(),

            )

        val request = ActivityTransitionRequest(transitions)

        val myPendingIntent = PendingIntent.getBroadcast(
            this,
            101,
            Intent(this, TransitionReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val task = ActivityRecognition.getClient(this)
                .requestActivityTransitionUpdates(request, myPendingIntent)

            task.addOnSuccessListener {
                Log.d("ActivityRecognition", "Successfully registered for transitions")
            }

            task.addOnFailureListener { e ->
                Log.e("ActivityRecognition", "Failed to register for transitions: ${e.message}")
            }
        }

    }

    private fun goToPermissionsActivity(){
        val intent = Intent(this, PermissionsActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_logout -> {
                //optionDisabledByAdmin()
                logout() // Show a confirmation dialog before logging out
                true
            }
            R.id.action_settings -> {
                openAppSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        cancelAlarm(this)

        Toast.makeText(this, getString(R.string.toast_tracking_stopped), Toast.LENGTH_SHORT).show()

        //clean all shared preferences
        val preferences = EncryptedPrefsManager.getPreferences()
        val editor = preferences.edit()
        editor.clear()
        editor.apply()

        //we send an intent to our service to start the location tracking
        val intentToStopLocationService = Intent(this@MainActivity, LocationService::class.java)
        intentToStopLocationService.action = LocationService.ACTION_STOP
        startService(intentToStopLocationService)

        // Redirect to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume")
        cancelAlarm(this)
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy")
    }

    //this will be useful later on. Trust me
    fun requestPermissionsFromFragment() : Boolean {
        return permissionHandler.checkAndRequestPermissions(this)
    }

    private fun cancelAlarm(context: Context) {
        Log.d("MainActivity", "Canceling alarm")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MyAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            201,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent) // Cancels the alarm
    }

    private fun sendStartingIntent(){
        val preferences = EncryptedPrefsManager.getPreferences()

        //we send an intent to our service to start the location tracking
        val intent = Intent(applicationContext, LocationService::class.java)
        intent.action = LocationService.ACTION_START

        applicationContext?.startService(intent)
        Toast.makeText(applicationContext, getString(R.string.toast_tracking_started), Toast.LENGTH_SHORT).show()
    }

    private fun saveCrashLog(exception: Throwable) {
        try {
            // private file storage
            val logFile = File(filesDir, "crash_logs.txt")
            // public file storage
            // Path to public Downloads directory
            val publicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val publicLogFile = File(publicDir, "crash_logs_public.txt")

            // Ensure directory exists
            if (!publicDir.exists()) publicDir.mkdirs()

            // Limit log file size (e.g., 5 MB)
            if (logFile.exists() && logFile.length() > 5 * 1024 * 1024) {
                logFile.delete() // Clear the log if it exceeds 5 MB
            }

            if (publicLogFile.exists() && publicLogFile.length() > 5 * 1024 * 1024) {
                publicLogFile.delete() // Clear the log if it exceeds 5 MB
            }

            val exceptionSignature = exception.stackTraceToString()
            if (loggedExceptions.contains(exceptionSignature)) {
                // Skip logging if the exception has already been logged
                return
            }
            loggedExceptions.add(exceptionSignature)

            // Write private log
            val privateWriter = FileWriter(logFile, true) // Append to the existing log
            privateWriter.appendLine("Crash Time: ${Date()}")
            privateWriter.appendLine("Thread: ${Thread.currentThread().name}")
            privateWriter.appendLine("Exception: ${exception.stackTraceToString()}")
            privateWriter.appendLine("=".repeat(50))
            privateWriter.close()

            // Write public log
            val publicWriter = FileWriter(publicLogFile, true) // Append mode
            publicWriter.appendLine("Crash Time: ${Date()}")
            publicWriter.appendLine("Thread: ${Thread.currentThread().name}")
            publicWriter.appendLine("Exception: ${exception.stackTraceToString()}")
            publicWriter.appendLine("=".repeat(50))
            publicWriter.close()

        } catch (e: IOException) {
            e.printStackTrace() // Log errors while writing logs
        }
    }
    //Access the logs at /data/data/<your.package.name>/files/crash_logs.txt using a file explorer on the test devices.

}