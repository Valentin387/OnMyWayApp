package com.sindesoft.onmywayapp.ui.auth

import android.content.Intent
import android.credentials.GetCredentialRequest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.FragmentManager.TAG
import androidx.lifecycle.coroutineScope
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.sindesoft.onmywayapp.databinding.ActivityLoginBinding
import com.sindesoft.onmywayapp.ui.main.MainActivity
import com.sindesoft.onmywayapp.utils.EncryptedPrefsManager
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.sindesoft.onmywayapp.BuildConfig
import kotlinx.coroutines.launch

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

        if (checkStoredToken()){
            goToMainActivity()
            return
        }

        /*val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)*/

        binding.btLogin.setOnClickListener {
            //performLogin()
            signInWithGoogleId()
        }

        //signInWithGoogleId()

    }

    private fun checkStoredToken() : Boolean {
        //check is there is a token stored
        //val preferences = getSharedPreferences("defaultPrefs", MODE_PRIVATE)
        val preferences = EncryptedPrefsManager.getPreferences()
        val googleIdToken = preferences.getString("google_id_token", null)
        Log.d("LoginActivity", "google_id_token: $googleIdToken")
        return googleIdToken != null
    }

    private fun createSessionPreference(googleIdToken: String){
        //val preferences = getSharedPreferences("defaultPrefs", MODE_PRIVATE)
        val preferences = EncryptedPrefsManager.getPreferences()
        val editor = preferences.edit()
        editor.putString("google_id_token", googleIdToken)

        editor.apply()
    }

    private fun signInWithGoogleId(){


        val webClientId = BuildConfig.WEB_APPLICATION_CLIENT_ID

        val googleIdOption: GetSignInWithGoogleOption = GetSignInWithGoogleOption.Builder(
            webClientId
        ).build()

        val credentialManager = CredentialManager.create(this)

        val request = androidx.credentials.GetCredentialRequest(
            listOf(googleIdOption)
        )

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@LoginActivity,
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                handleFailure(e)
            }

        }
    }

    private fun handleFailure(e: GetCredentialException) {
        // Handle the error.
        Log.e("Error getting credential", e.toString())
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        val credential = result.credential

        when (credential) {
            is PublicKeyCredential -> {
                val responseJson = credential.authenticationResponseJson
                // Share responseJson i.e. a GetCredentialResponse on your server to
                // validate and  authenticate
            }

            is PasswordCredential -> {
                val username = credential.id
                val password = credential.password
                // Use id and password to send to your server to validate
                // and authenticate
            }

            is CustomCredential -> {
                // Handle custom credential type here.
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        val googleIdToken = googleIdTokenCredential.idToken

                        Log.i("GoogleIdToken", googleIdToken)

                        // Send the ID token to your server for validation and authentication
                        //validateGoogleIdTokenOnServer(googleIdToken)

                        // Save the Google ID token in the shared preferences
                        createSessionPreference(googleIdToken)

                        //Navigate to the main activity
                        goToMainActivity()

                    } catch (e: Exception) {
                        Log.e("GoogleIdTokenError", "Failed to parse Google ID token", e)
                    }
                }else{
                    Log.e("CustomCredential", "Unexpected custom credential type: ${credential.type}")
                }

            }
            else -> {
                // Catch any unrecognized credential type here.
                Log.e("Unexpected type of credential", credential.toString())
            }
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