package com.valentinConTilde.onmywayapp.utils


import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object EncryptedPrefsManager {
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        if (!EncryptedPrefsManager::sharedPreferences.isInitialized) {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            sharedPreferences = EncryptedSharedPreferences.create(
                context,
                "defaultPrefs_crypto",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }

        //val preferences = context.getSharedPreferences("defaultPrefs", MODE_PRIVATE)
    }

    fun getPreferences(): SharedPreferences {
        return sharedPreferences
    }
}