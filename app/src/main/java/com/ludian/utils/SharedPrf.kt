package com.ludian.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.ludian.R
import com.ludian.models.LoginModel


class SharedPrf constructor(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
    private val USER = "LUDIANUser"
    fun getStoredTag(str: String): String {
        return prefs.getString(str, "")!!
    }

    fun setStoredTag(str: String, query: String) {
        prefs.edit().putString(str, query).apply()
    }

    fun getUser(): LoginModel.UserData {
        return try {
            val gson = Gson()
            val json: String? = prefs.getString(USER, "")
            val obj: LoginModel.UserData =
                gson.fromJson(json, LoginModel.UserData::class.java)
            obj
        } catch (e: Exception) {
            LoginModel.UserData("0", "","","","","","")
        }
    }

    fun setUser(query: LoginModel.UserData) {
        val gson = Gson()
        val json = gson.toJson(query)
        prefs.edit().putString(USER, json).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        const val LOGIN: String = "login"
        const val USER_ID: String = "user_id"
        const val TOKEN: String = "token"
        const val FIREBASE_TOKEN: String = "firebase_token"
        const val LATTITUDE: String = "lat"
        const val LONGITUDE: String = "long"
        const val DOC_STATUS: String = "doc_status"
        const val ADDRESS: String = "address"
        const val REQUEST_ID: String = "request_id"
        const val LANGUAGE: String = "language"

    }

}
