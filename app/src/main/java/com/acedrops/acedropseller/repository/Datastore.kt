package com.acedrops.acedropseller.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.acedrops.acedropseller.model.UserData
import kotlinx.coroutines.flow.first

const val DATASTORE_NAME = "user_details"
val Context.datastore: DataStore<Preferences> by preferencesDataStore(DATASTORE_NAME)

class Datastore(context: Context) {
    private val appContext = context.applicationContext

    companion object {
        const val LOGIN_KEY = "login_key"
        const val NAME_KEY = "name_key"
        const val SHOP_NAME_KEY = "shop_name_key"
        const val ADDRESS_KEY = "address_key"
        const val NO_OF_MEMBERS = "no_of_members_key"
        const val DESC_KEY = "desc_key"
        const val PHN_KEY = "phn_key"
        const val EMAIL_KEY = "email_key"
        const val IMAGE_URL = "email_url"
        const val ACCESS_TOKEN_KEY = "token_key"
        const val REF_TOKEN_KEY = "ref_token_key"
        const val ID = "seller_id"
        const val GOOGLE_ID = "google_id"
    }

    suspend fun saveUserDetails(key: String, value: String?) {
        val key1 = stringPreferencesKey(key)
        appContext.datastore.edit { user_details ->
            user_details[key1] = value.toString()
        }
    }

    suspend fun changeLoginState(value: Boolean) {
        val key1 = booleanPreferencesKey(LOGIN_KEY)
        appContext.datastore.edit {
            it[key1] = value
        }
    }

    suspend fun getUserDetails(key: String): String? {
        val key1 = stringPreferencesKey(key)
        return appContext.datastore.data.first()[key1]
    }

    suspend fun isLogin(): Boolean {
        val key1 = booleanPreferencesKey(LOGIN_KEY)
        return appContext.datastore.data.first()[key1] ?: false
    }

    //Login true and save all data to Datastore
    suspend fun saveToDatastore(it: UserData, context: Context) {
        val datastore = Datastore(context)
        datastore.changeLoginState(true)
        datastore.saveUserDetails(EMAIL_KEY, it.email!!)
        datastore.saveUserDetails(NAME_KEY, it.name!!)
        if (it.googleId == null) datastore.saveUserDetails(GOOGLE_ID, null)
        else datastore.saveUserDetails(GOOGLE_ID, it.googleId)
        datastore.saveUserDetails(ACCESS_TOKEN_KEY, it.access_token!!)
        datastore.saveUserDetails(REF_TOKEN_KEY, it.refresh_token!!)
        datastore.saveUserDetails(ID, it.id.toString())
    }
}

