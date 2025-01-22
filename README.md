# OnMyWayApp

## Project Overview
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

---

Feel free to reach out for any questions or contributions!

