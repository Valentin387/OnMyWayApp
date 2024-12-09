import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.sindesoft.onmywayapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sindesoft.onmywayapp"
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

            // Load the properties from the local.properties file
            val properties = Properties()
            properties.load(FileInputStream(rootProject.file("local.properties")))

            buildConfigField("String", "BASE_URL", "\"${properties.getProperty("BASE_URL")}\"")
            buildConfigField("String", "ANDROID_CLIENT_ID", "\"${properties.getProperty("ANDROID_CLIENT_ID")}\"")
            buildConfigField("String", "WEB_APPLICATION_CLIENT_ID", "\"${properties.getProperty("WEB_APPLICATION_CLIENT_ID")}\"")
            buildConfigField("String", "GOOGLE_ID_TOKEN_SAMPLE", "\"${properties.getProperty("GOOGLE_ID_TOKEN_SAMPLE")}\"")
            buildConfigField("String", "BAD_GOOGLE_ID_TOKEN", "\"${properties.getProperty("BAD_GOOGLE_ID_TOKEN")}\"")
            signingConfig = signingConfigs.getByName("debug")
        }

        debug{
            // Load the properties from the local.properties file
            val properties = Properties()
            properties.load(FileInputStream(rootProject.file("local.properties")))

            buildConfigField("String", "BASE_URL", "\"${properties.getProperty("BASE_URL")}\"")
            buildConfigField("String", "ANDROID_CLIENT_ID", "\"${properties.getProperty("ANDROID_CLIENT_ID")}\"")
            buildConfigField("String", "WEB_APPLICATION_CLIENT_ID", "\"${properties.getProperty("WEB_APPLICATION_CLIENT_ID")}\"")
            buildConfigField("String", "GOOGLE_ID_TOKEN_SAMPLE", "\"${properties.getProperty("GOOGLE_ID_TOKEN_SAMPLE")}\"")
            buildConfigField("String", "BAD_GOOGLE_ID_TOKEN", "\"${properties.getProperty("BAD_GOOGLE_ID_TOKEN")}\"")
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


}