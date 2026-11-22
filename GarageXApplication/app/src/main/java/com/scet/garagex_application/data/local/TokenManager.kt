package com.scet.garagex_application.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Creates a single instance of Preferences DataStore for the app context.
// DataStore is Google's recommended replacement for SharedPreferences:
// it is asynchronous, safe to call from coroutines, and handles data migration.
val Context.dataStore by preferencesDataStore(name = "garejx_preferences")

/**
 * TokenManager is a simple helper class responsible for securely saving,
 * reading, and clearing the user's login session (JWT token and Role).
 */
class TokenManager(private val context: Context) {

    companion object {
        // Preference Keys
        val KEY_JWT_TOKEN = stringPreferencesKey("jwt_token")
        val KEY_USER_ROLE = stringPreferencesKey("user_role") // "USER", "MECHANIC", or "ADMIN"
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_SERVER_URL = stringPreferencesKey("custom_server_url")

        // Default base URL for Android Emulator pointing to host PC Express backend
        const val DEFAULT_BASE_URL = "http://10.0.2.2:5000/api/"
    }

    /**
     * Saves the authentication details when login or registration is successful.
     */
    suspend fun saveAuthSession(token: String, role: String, name: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_JWT_TOKEN] = token
            preferences[KEY_USER_ROLE] = role
            preferences[KEY_USER_NAME] = name
        }
    }

    /**
     * Reads the current JWT token as a reactive Flow.
     * Returns null if user is not logged in.
     */
    fun getToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_JWT_TOKEN]
        }
    }

    /**
     * Synchronous check used during app boot to see if a token exists.
     */
    suspend fun getStoredToken(): String? {
        return context.dataStore.data.map { it[KEY_JWT_TOKEN] }.first()
    }

    /**
     * Reads the user role ("USER" for customer, "MECHANIC" for garage technician).
     */
    fun getUserRole(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_USER_ROLE]
        }
    }

    /**
     * Reads the cached user's name.
     */
    fun getUserName(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_USER_NAME]
        }
    }

    /**
     * Reads the active backend URL. If not customized, returns the default emulator URL.
     * This allows any user installing the APK to connect to their own Wi-Fi IP address!
     */
    fun getServerUrl(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_SERVER_URL] ?: DEFAULT_BASE_URL
        }
    }

    /**
     * Saves a new backend IP/URL (e.g. from the in-app server settings dialog).
     */
    suspend fun saveServerUrl(url: String) {
        // Ensure trailing slash so Retrofit can resolve relative endpoints correctly
        val formattedUrl = if (url.endsWith("/")) url else "$url/"
        context.dataStore.edit { preferences ->
            preferences[KEY_SERVER_URL] = formattedUrl
        }
    }

    /**
     * Clears all session data upon logout.
     */
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_JWT_TOKEN)
            preferences.remove(KEY_USER_ROLE)
            preferences.remove(KEY_USER_NAME)
        }
    }
}
