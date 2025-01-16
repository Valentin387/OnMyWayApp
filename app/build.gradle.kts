import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.mapsplatform.secrets.gradle)
}

buildscript {
    dependencies {
        classpath(libs.secrets.gradle.plugin)
    }
}

secrets {
    // To add your Maps API key to this project:
    // 1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
    // 2. Add this line, where YOUR_API_KEY is your API key:
    //        MAPS_API_KEY=YOUR_API_KEY
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"

    // Configure which keys should be ignored by the plugin by providing regular expressions.
    // "sdk.dir" is ignored by default.
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
}


android {
    namespace = "com.valentinConTilde.onmywayapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.valentinConTilde.onmywayapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            /*// Load the properties from the local.properties file
            val properties = Properties()
            properties.load(FileInputStream(rootProject.file("local.properties")))

            buildConfigField("String", "BASE_URL", "\"${properties.getProperty("BASE_URL")}\"")
            buildConfigField("String", "ANDROID_CLIENT_ID", "\"${properties.getProperty("ANDROID_CLIENT_ID")}\"")
            buildConfigField("String", "WEB_APPLICATION_CLIENT_ID", "\"${properties.getProperty("WEB_APPLICATION_CLIENT_ID")}\"")
            buildConfigField("String", "GOOGLE_ID_TOKEN_SAMPLE", "\"${properties.getProperty("GOOGLE_ID_TOKEN_SAMPLE")}\"")
            buildConfigField("String", "BAD_GOOGLE_ID_TOKEN", "\"${properties.getProperty("BAD_GOOGLE_ID_TOKEN")}\"")*/
            signingConfig = signingConfigs.getByName("debug")
        }

        debug{
            /*// Load the properties from the local.properties file
            val properties = Properties()
            properties.load(FileInputStream(rootProject.file("local.properties")))

            buildConfigField("String", "BASE_URL", "\"${properties.getProperty("BASE_URL")}\"")
            buildConfigField("String", "ANDROID_CLIENT_ID", "\"${properties.getProperty("ANDROID_CLIENT_ID")}\"")
            buildConfigField("String", "WEB_APPLICATION_CLIENT_ID", "\"${properties.getProperty("WEB_APPLICATION_CLIENT_ID")}\"")
            buildConfigField("String", "GOOGLE_ID_TOKEN_SAMPLE", "\"${properties.getProperty("GOOGLE_ID_TOKEN_SAMPLE")}\"")
            buildConfigField("String", "BAD_GOOGLE_ID_TOKEN", "\"${properties.getProperty("BAD_GOOGLE_ID_TOKEN")}\"")*/
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // More dependencies
    // security.crypto
    implementation(libs.androidx.security.crypto)

    //convert the dependencies above to Toml format
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    //retrofit HTTP requests
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson.converter)

    //Glide dependency
    implementation(libs.glide)

    //Ktor - I think I never really used these 2:
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)

    //location services
    implementation(libs.google.play.services.location)

    // swiperefreshlayout
    implementation(libs.androidx.swiperefreshlayout)

    //WorkManager

    // (Java only)
    implementation(libs.androidx.work.runtime)

    // Kotlin + coroutines
    implementation(libs.androidx.work.runtime.ktx)

    // optional - RxJava2 support
    implementation(libs.androidx.work.rxjava2)

    // optional - GCMNetworkManager support
    implementation(libs.androidx.work.gcm)

    // optional - Test helpers
    androidTestImplementation(libs.androidx.work.testing)

    // optional - Multiprocess support
    implementation(libs.androidx.work.multiprocess)

    // Maps SDK for Android
    implementation("com.google.android.gms:play-services-maps:19.0.0")


}