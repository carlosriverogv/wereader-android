package tfg.carlos.wereaderapp

import android.app.Application
import tfg.carlos.wereaderapp.data.network.SessionManager

class WeReaderApplication: Application() {
    companion object {
        lateinit var instance: WeReaderApplication
            private set

        val sessionManager: SessionManager by lazy {
            SessionManager(instance.applicationContext)
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}