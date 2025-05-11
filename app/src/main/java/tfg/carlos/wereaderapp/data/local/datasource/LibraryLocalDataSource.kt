package tfg.carlos.wereaderapp.data.local.datasource

import kotlinx.coroutines.flow.Flow
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.entity.BookEntity
import tfg.carlos.wereaderapp.data.local.BookDao

class LibraryLocalDataSource(private val dao: BookDao) {
    private val sessionManager by lazy {
        WeReaderApplication.sessionManager
    }

    fun getBooksFlow(): Flow<List<BookEntity>> = dao.getAllBooks()
    fun getMyBooksFlow(): Flow<List<BookEntity>> = dao.getMyBooks(getIdUser())
    fun getSharedBooksFlow(): Flow<List<BookEntity>> = dao.getSharedBooks(getIdUser())

    suspend fun getBookById(id: String): BookEntity {
        return dao.getBookById(id, getIdUser())
    }

    suspend fun insertBooks(books: List<BookEntity>) {
        dao.insertAll(books)
    }

    suspend fun cacheBooks(newBooks: List<BookEntity>) {
        val existingBooks = dao.getAllBooksOnce()

        // Merge de campos locales
        val mergedBooks = newBooks.map { newBook ->
            val old = existingBooks.find { it.id == newBook.id && it.idUser == newBook.idUser }
            newBook.copy(
                isReading = old?.isReading ?: false,
                isPending = old?.isPending ?: false,
                readingProgress = old?.readingProgress ?: 0,
                mine = newBook.mine,
                idUser = newBook.idUser
            )
        }

        // Ya no se borra toda la tabla
        dao.insertAll(mergedBooks)
    }

    suspend fun updateBook(book: BookEntity) {
        dao.updateBook(book)
    }

    fun getPendingBooks(): Flow<List<BookEntity>> {
        return dao.getPendingBooks(getIdUser())
    }

    fun getReadingBooks(): Flow<List<BookEntity>> {
        return dao.getReadingBooks(getIdUser())
    }

    // Obtiene el ID de usuario de la sesión
    private fun getIdUser(): String {
        return sessionManager.getUserId()
    }
}