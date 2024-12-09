package com.sindesoft.onmywayapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
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
import com.sindesoft.onmywayapp.utils.EncryptedPrefsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }
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

}