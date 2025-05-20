package tfg.carlos.wereaderapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import tfg.carlos.wereaderapp.data.entity.BookEntity
import tfg.carlos.wereaderapp.data.entity.StoreBookEntity
import tfg.carlos.wereaderapp.data.local.interfaces.BookDao
import tfg.carlos.wereaderapp.data.local.interfaces.StoreBookDao

@Database(entities = [BookEntity::class, StoreBookEntity::class], version = 9, exportSchema = false)
abstract class LibraryDB: RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun storeBookDao(): StoreBookDao
}