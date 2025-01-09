package com.sindesoft.onmywayapp.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.provider.Settings
import android.view.MenuItem
import android.widget.ImageView
import com.google.android.material.snackbar.Snackbar
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
import com.sindesoft.onmywayapp.R
import com.sindesoft.onmywayapp.data.models.User
import com.sindesoft.onmywayapp.databinding.ActivityMainBinding
import com.sindesoft.onmywayapp.databinding.NavHeaderMainBinding
import com.sindesoft.onmywayapp.ui.auth.LoginActivity
import com.sindesoft.onmywayapp.ui.permissions.PermissionsActivity
import com.sindesoft.onmywayapp.utils.CustomPermissionHandler
import com.sindesoft.onmywayapp.utils.EncryptedPrefsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionHandler: CustomPermissionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        //clean all shared preferences
        val preferences = EncryptedPrefsManager.getPreferences()
        val editor = preferences.edit()
        editor.clear()
        editor.apply()

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
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop")
    }

    //this will be useful later on. Trust me
    fun requestPermissionsFromFragment() : Boolean {
        return permissionHandler.checkAndRequestPermissions(this)
    }


}