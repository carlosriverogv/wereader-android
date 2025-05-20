package tfg.carlos.wereaderapp.data.local.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import tfg.carlos.wereaderapp.data.entity.StoreBookEntity

@Dao
interface StoreBookDao {
    @Query("SELECT * FROM store_books")
    fun getAllStoreBooks(): Flow<List<StoreBookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<StoreBookEntity>)

    @Query("DELETE FROM store_books")
    suspend fun clearStoreBooks()
}