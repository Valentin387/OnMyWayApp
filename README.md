# OnMyWayApp
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.20-blueviolet?style=for-the-badge&logo=kotlin)
![Android](https://img.shields.io/badge/Android-14-green?style=for-the-badge&logo=android)

## Project Overview
This application is designed to track the location of users in real time. This prototype integrates various components such as network calls, local storage, background services, activity recognition and alarm manager.
OnMyWayApp is a Kotlin-based Android application that integrates Google Services for authentication and Google Maps API for location-based functionalities. This README outlines the setup process and the requirements to get the project running in your local development environment.

## Prerequisites
Before starting, ensure you have:
- Android Studio installed
- Gradle configured in your environment
- Access to Google Cloud Console to create and manage API keys and OAuth credentials

## Setup Instructions

### 1. Clone the Repository
Clone the project repository to your local machine:
```bash
git clone <repository_url>
cd OnMyWayApp
```

### 2. Create `secrets.properties`
In the project's root directory, create a file named `secrets.properties`. This file will store your application's secrets and must never be added to version control.

#### Example `secrets.properties` Content:
```properties
BASE_URL=https://A.com/
ANDROID_CLIENT_ID=A-A.A.googleusercontent.com
WEB_APPLICATION_CLIENT_ID=A-A.A.googleusercontent.com
GOOGLE_ID_TOKEN_SAMPLE=A.A.A-A-A-A-A--A-A
BAD_GOOGLE_ID_TOKEN=A.A.A-w-A-A-A--A-A
MAPS_API_KEY=DEFAULT_API_KEY
```

#### Explanation of Secrets:
- **BASE_URL**: The base URL of your server hosting the API endpoints.
- **ANDROID_CLIENT_ID**: The client ID for your Android app from Google Cloud Console.
- **WEB_APPLICATION_CLIENT_ID**: The client ID for your web app from Google Cloud Console.
- **GOOGLE_ID_TOKEN_SAMPLE**: A sample Google ID token for testing authentication.
- **BAD_GOOGLE_ID_TOKEN**: A bad Google ID token for testing error cases in authentication.
- **MAPS_API_KEY**: The API key for integrating Google Maps SDK.

You can find a reference for some of these keys in the `local.defaults.properties` file.

### 3. Generate and Configure Credentials
1. **Google Cloud Console:**
    - Navigate to [Google Cloud Console](https://console.cloud.google.com/).
    - Enable the **Google Maps SDK for Android** and **Google Authentication API**.
    - For the Maps API key, restrict it to Android apps and the Maps SDK.

2. **Create OAuth Credentials:**
    - Follow the instructions in the Google Cloud Console to create your Android and Web OAuth 2.0 client IDs.

3. **Provide App Details:**
    - Package Name: `com.valentinConTilde.onmywayapp`
    - SHA-1 Key: Generate this using the steps below.

### 4. Obtain SHA-1 Key
To generate your SHA-1 key:
1. Open Android Studio.
2. Navigate to the Gradle Console (click the elephant icon in the top-right corner, then find the console icon).
3. Run the following command:
   ```bash
   gradle signingReport
   ```
4. Copy the generated SHA-1 key and add it to your credentials in the Google Cloud Console.

### 5. Notes on Key Management
- The SHA-1 key is specific to your local development environment and Android Studio installation.
- If you reinstall Android Studio or reformat your PC, the SHA-1 key will change. Update it in Google Cloud Console and regenerate the app bundle.
- To avoid conflicts, uninstall the previous version of the app on your mobile device before installing the updated version.

## Important Considerations
- **`secrets.properties` is not included in version control.** Ensure it is excluded in your `.gitignore` file.
- If you choose to leave `GOOGLE_ID_TOKEN_SAMPLE` and `BAD_GOOGLE_ID_TOKEN` blank, you can still proceed, but these are helpful for testing error scenarios in the authentication module.

## Overview
### Main Features
- **Subscription mechanism**
  - Each user gets a 6 digits code, you can share this code with someone so they subscribe to you, once they have subscribed, they will be able to monitor your location 24/7
  - There are views in the app to display either your subscriptions (people you follow) and your subscribers (people who follow you)
  - At any moment you can remove any of your subscriptions or subscribers (privacy concerns) Note: if you delete X person but this person still has your 6 digit code, they can re-subscribe to you. You would have to crate another account with another gmail email or directly modify your 6 digit code in the database

- **Web Socket Integration**:
  - Real time tracking of your subscriptions on the map, you get to see useful information like speed and battery percentage
  - You can select a user from your subscriptions, define a date range and fetch all the routes this user has made in that date range.

- **Foreground Location Service**:
   - High-accuracy location tracking.
   - Implements algorithms to filter location noise and ensure reliable data.
   -  It continues working in the background, even if the app is destroyed.

- **Motion Detection**:
   - Automatically detects lack of movement using Activity Recognition.
   - Triggers a notification to the user.

- **Background Tasks Using Work Managers**:
   - **Real-Time Location Sender**: Continuously sends filtered location data to the server in real time with an automatic fallback mechanism for offline mode.
   - **Background Tasks Using Work Managers**:
   - **Real-Time Location Sender**: Continuously sends filtered location data to the server in real time with an automatic fallback mechanism for offline mode.
   - **Alarm Manager Feature**: If the user destroys the app (removes it from the background apps) without logging out first, the system activates a mechanism of periodic self-scheduled alarms every minute. Without disturbing the user, these alarms trigger the Activity Recognition API to check if the user is moving. If the user is not moving, the system restarts the foreground location service with a foreground notification visible to the user.

- **Data Encryption**:
   - All personal information about the user stored locally is encrypted using `EncryptedSharedPreferences`.

### Configuration

#### `defaultConfig` in `build.gradle.kts`
```kotlin
defaultConfig {  
    applicationId = "com.example.app02_v01"  
    minSdk = 24  
    targetSdk = 34  
    versionCode = 1  
    versionName = "1.0"  

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"  
}  
``` 

#### Kotlin Version
```properties
kotlin = "2.0.20"  
```  

### Technology Stack
- **Web Sockets**: Ktor Web Sockets version "2.3.13"
- **Google Maps API**
- **Google oAuth 2.0**
- **Android SDK**: Min SDK 24, Target SDK 34
- **Kotlin**: 2.0.20
- **Work Manager**: For background tasks.
- **EncryptedSharedPreferences**: For data encryption.
- **Activity Recognition**: For motion detection.
- **Alarm Manager**: For experimental background task.


## And what about the backend for this beautiful frontend?
Don't worry, I got you covered!! I am releasing the backend code as well, just clone this other repo and follow the readme instructions:

```properties
https://github.com/Valentin387/OnMyWayServer
```  

---

Feel free to reach out for any questions or contributions!

