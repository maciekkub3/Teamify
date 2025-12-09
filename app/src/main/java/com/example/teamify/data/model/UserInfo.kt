package com.example.teamify.data.model

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore("user_info")

class UserInfo @Inject constructor(
    private val context: Context
) {
    val role = context.dataStore.data.map { preferences ->
        preferences[ROLE_KEY]
    }
    val email = context.dataStore.data.map { preferences ->
        preferences[EMAIL_KEY]
    }
    val name = context.dataStore.data.map { preferences ->
        preferences[NAME_KEY]
    }
    val id = context.dataStore.data.map { preferences ->
        preferences[ID_KEY]
    }
    val imageUrl = context.dataStore.data.map { preferences ->
        preferences[ID_KEY]
    }


    suspend fun updateUserInfo(name: String, email: String, role: String, id: String, imageUrl: String?) {
        context.dataStore.edit { settings ->
            settings[ROLE_KEY] = role
            settings[EMAIL_KEY] = email
            settings[NAME_KEY] = name
            settings[ID_KEY] = id
            if (imageUrl != null) {
                settings[IMAGE_URL_KEY] = imageUrl
            }
        }
    }

    companion object {
        val ROLE_KEY = stringPreferencesKey("role_key")
        val EMAIL_KEY = stringPreferencesKey("email_key")
        val NAME_KEY = stringPreferencesKey("name_key")
        val ID_KEY = stringPreferencesKey("id_key")
        val IMAGE_URL_KEY = stringPreferencesKey("image_url_key")
    }
}
