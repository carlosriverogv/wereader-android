package tfg.carlos.wereaderapp.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)

    companion object {
        private const val USER_TOKEN = "USER_TOKEN"
    }

    fun saveToken(token: String) {
        prefs.edit().putString(USER_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun clearToken() {
        prefs.edit().remove(USER_TOKEN).apply()
    }
}