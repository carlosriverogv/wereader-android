package tfg.carlos.wereaderapp.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import tfg.carlos.wereaderapp.data.entity.BookEntity

@Database(entities = [BookEntity::class], version = 3)
abstract class LibraryDB: RoomDatabase() {
    abstract fun bookDao(): BookDao
}

@Dao
interface BookDao {
    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books")
    suspend fun getAllBooksOnce(): List<BookEntity>

    @Query("SELECT * FROM books WHERE mine = 1")
    fun getMyBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE mine = 0")
    fun getSharedBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :id LIMIT 1")
    suspend fun getBookById(id: String): BookEntity?

    @Query("SELECT * FROM books WHERE isReading = 1")
    fun getReadingBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isPending = 1")
    fun getPendingBooks(): Flow<List<BookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<BookEntity>)

    @Query("DELETE FROM books")
    suspend fun clearBooks()

    @Update
    suspend fun updateBook(book: BookEntity)
}