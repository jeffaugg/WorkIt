package pies3.workit.data.local

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val tokenKey = stringPreferencesKey("auth_token")
    private val userIdKey = stringPreferencesKey("user_id")

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }

        try {
            val userId = decodeJwtUserId(token)
            if (userId != null) {
                saveUserId(userId)
            }
        } catch (e: Exception) {
        }
    }

    fun getTokenFlow(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[tokenKey]
        }
    }

    suspend fun getToken(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[tokenKey]
        }.first()
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(tokenKey)
            preferences.remove(userIdKey)
        }
    }

    fun isLoggedIn(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[tokenKey] != null
        }
    }

    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[userIdKey] = userId
        }
    }

    suspend fun getUserId(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[userIdKey]
        }.first()
    }


    private fun decodeJwtUserId(token: String): String? {
        try {
            val parts = token.split(".")
            if (parts.size != 3) return null

            val payload = parts[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP)
            val decodedString = String(decodedBytes)

            val jsonObject = JSONObject(decodedString)
            return jsonObject.optString("id", null)
        } catch (e: Exception) {
            return null
        }
    }
}
