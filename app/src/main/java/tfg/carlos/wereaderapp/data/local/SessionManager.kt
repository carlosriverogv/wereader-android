package tfg.carlos.wereaderapp.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)

    companion object {
        private const val USER_TOKEN = "USER_TOKEN"
        private const val USER_ID = "USER_ID"

        private const val SHARED_LIBRARY = "isSharingLibrary"
        private const val SHARED_USER_ID = "sharedUserId"
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

    // Shared Library Management
    fun saveSharingLibrary(isSharing: Boolean, friendUserId: String?) {
        prefs.edit()
            .putBoolean(SHARED_LIBRARY, isSharing)
            .putString(SHARED_USER_ID, friendUserId)
            .apply()
    }

    fun clearSharingLibrary() {
        prefs.edit()
            .remove(SHARED_LIBRARY)
            .remove(SHARED_USER_ID)
            .apply()
    }

    fun isSharingLibrary(): Boolean {
        return prefs.getBoolean(SHARED_LIBRARY, false)
    }

    fun getSharedUserId(): String? {
        return prefs.getString(SHARED_USER_ID, null)
    }
}