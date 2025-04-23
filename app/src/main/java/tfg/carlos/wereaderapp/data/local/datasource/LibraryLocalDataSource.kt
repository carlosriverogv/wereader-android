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

    suspend fun cacheBooks(newBooks: List<BookEntity>) {
        val existingBooks = dao.getAllBooksOnce()

        // Merge de campos locales con los nuevos
        val mergedBooks = newBooks.map { newBook ->
            val old = existingBooks.find { it.id == newBook.id }
            newBook.copy(
                isReading = old?.isReading ?: false,
                isPending = old?.isPending ?: false,
                readingProgress = old?.readingProgress ?: 0
            )
        }

        dao.clearBooks()
        dao.insertAll(mergedBooks)
    }

    suspend fun getBookById(id: String): BookEntity? {
        return dao.getBookById(id)
    }

    suspend fun updateBook(book: BookEntity) {
        dao.updateBook(book)
    }

    fun getPendingBooks(): Flow<List<BookEntity>> {
        return dao.getPendingBooks()
    }

    fun getReadingBooks(): Flow<List<BookEntity>> {
        return dao.getReadingBooks()
    }
}