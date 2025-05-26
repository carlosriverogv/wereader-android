package tfg.carlos.wereaderapp.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)

    companion object {
        private const val USER_TOKEN = "USER_TOKEN"
        private const val USER_ID = "USER_ID"
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

    fun saveUserId(userId: String) {
        prefs.edit()
            .putString(USER_ID, userId)
            .apply()
    }

    fun getUserId(): String {
        return prefs.getString(USER_ID, null)
            ?: throw IllegalStateException()
    }

    fun isSharingLibrary(): Boolean {
        return prefs.getBoolean("isSharingLibrary", false)
    }

    fun getSharedUserId(): String? {
        return prefs.getString("sharedUserId", null)
    }
}