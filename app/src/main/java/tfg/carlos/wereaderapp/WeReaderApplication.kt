package tfg.carlos.wereaderapp

import android.app.Application
import androidx.room.Room
import tfg.carlos.wereaderapp.data.local.LibraryDB
import tfg.carlos.wereaderapp.data.local.SessionManager

class WeReaderApplication: Application() {
    companion object {
        lateinit var instance: WeReaderApplication
            private set

        val sessionManager: SessionManager by lazy {
            SessionManager(instance.applicationContext)
        }
    }
    lateinit var weReaderDB: LibraryDB
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        weReaderDB = Room.databaseBuilder(
            this,
            LibraryDB::class.java,
            "WeReader-db"
        ).build()
    }
}