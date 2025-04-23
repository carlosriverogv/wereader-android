package tfg.carlos.wereaderapp.data.local.datasource

import kotlinx.coroutines.flow.Flow
import tfg.carlos.wereaderapp.data.entity.BookEntity
import tfg.carlos.wereaderapp.data.local.BookDao
import tfg.carlos.wereaderapp.data.model.book.BookItem

class LibraryLocalDataSource(private val dao: BookDao) {
    fun getBooksFlow(): Flow<List<BookEntity>> = dao.getAllBooks()

    suspend fun insertBooks(books: List<BookEntity>) {
        dao.insertAll(books)
    }

    suspend fun cacheBooks(bookItems: List<BookEntity>) {
        dao.clearBooks()
        dao.insertAll(bookItems)
    }
}