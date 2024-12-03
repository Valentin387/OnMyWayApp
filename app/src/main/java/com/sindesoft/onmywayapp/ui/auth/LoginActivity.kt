package com.sindesoft.onmywayapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.AppBarConfiguration
import com.sindesoft.onmywayapp.R
import com.sindesoft.onmywayapp.databinding.ActivityLoginBinding
import com.sindesoft.onmywayapp.ui.main.MainActivity
import com.sindesoft.onmywayapp.utils.EncryptedPrefsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityLoginBinding

/*    private val securityCodeLoginService : SecurityCodeLoginService by lazy{
        SecurityCodeLoginService.create(applicationContext)
    }*/


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // Initialize the EncryptedPrefsManager
        EncryptedPrefsManager.init(applicationContext)

        // Inflate the layout using ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)*/

        binding.btLogin.setOnClickListener {
            performLogin()
        }

    }

    private fun goToMainActivity(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    /*
    MAIN - hilo principal
    IO - operaciones de entrada y salida, como retrofit
    DEFAULT - operaciones de CPU intensivas
     */

    private fun performLogin(){

        /*val etEmail = binding.etUserEmail.text.toString()
        val etPassword = binding.etUserPassword.text.toString()*/

        val etEmail = binding.etEmail.text.toString()
        val etPassword = binding.etUserPassword.text.toString()

        goToMainActivity()

        /*
        if (etEmail.isEmpty() || etPassword.isEmpty()){
            Toast.makeText(applicationContext,"Fill all the fields please", Toast.LENGTH_SHORT).show()
            return
        }

        // Launch a coroutine to perform the login
        lifecycleScope.launch(Dispatchers.IO){
            try{
                val loginRequest = LoginRequest(code = etSecurityCode)
                val response = securityCodeLoginService.postLogin(loginRequest)

                if(response.isSuccessful) {
                    val loginResponse = response.body()!!
                    *//*Log.d("LoginActivity", "JWT: ${loginResponse.token}")
                    Log.d("LoginActivity", "User: ${loginResponse.user}")
                    Log.d("LoginActivity","refresh: ${loginResponse.refresh}")*//*
                    createSessionPreference(loginResponse.token, loginResponse.user, loginResponse.refresh)

                    withContext(Dispatchers.Main){
                        goToMainActivity()
                    }
                }else{
                    withContext(Dispatchers.Main){
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.Invalid_credentials),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }catch(e: Exception){
                //Log.d("LoginActivity", e.toString())

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        applicationContext,
                        //"Exception: ${e.message}",
                        getString(R.string.Login_exception),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }*/
    }
}