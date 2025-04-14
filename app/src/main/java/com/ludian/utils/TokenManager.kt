package com.ludian.utils

import android.content.Context
import android.content.SharedPreferences
import com.ludian.utils.Constants.PREFS_TOKEN_FILE
import com.ludian.utils.Constants.USER_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext context: Context) {
  /*  private var prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_TOKEN_FILE, Context.MODE_PRIVATE)*/

    private val sharedPrf by lazy { SharedPrf(context) }


    fun saveToken(token: String) {
      sharedPrf.setStoredTag(USER_TOKEN,token)
    }

    fun getToken(): String? {
        return sharedPrf.getStoredTag(USER_TOKEN)
    }
}