package tfg.carlos.wereaderapp.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import tfg.carlos.wereaderapp.data.entity.BookEntity
import tfg.carlos.wereaderapp.data.local.datasource.LibraryLocalDataSource
import tfg.carlos.wereaderapp.data.model.book.BookItem
import tfg.carlos.wereaderapp.data.model.book.toEntity
import tfg.carlos.wereaderapp.data.remote.datasource.LibraryRemoteDadaSource

class LibraryRepository(
    val remote: LibraryRemoteDadaSource,
    val local: LibraryLocalDataSource
) {
    // API Methods
    suspend fun getAuthUserLibrary() = remote.getAuthUserLibrary()

    // ROOM Methods
    fun getLibraryBooks(): Flow<List<BookEntity>> = local.getBooksFlow()

    fun getReadingBooks(): Flow<List<BookEntity>> = local.getReadingBooks()

    fun getPendingBooks(): Flow<List<BookEntity>> = local.getPendingBooks()

    suspend fun insertBooks(books: List<BookEntity>) = local.insertBooks(books)

    suspend fun cacheBooks(bookItems: List<BookEntity>) {
        local.cacheBooks(bookItems)
    }

    suspend fun fetchAndCacheAuthUserLibrary(): List<BookItem> {
        val libraryResponse = remote.getAuthUserLibrary().first()
        Log.d("Repository", "Libros recibidos de API: ${libraryResponse.books.size}")
        val bookItems = libraryResponse.books

        val entities = bookItems.map { it.toEntity() }
        Log.d("Repository", "Entidades generadas: ${entities.size}")
        local.cacheBooks(entities)

        return bookItems
    }

    suspend fun updateBookReadingStatus(id: String, isReading: Boolean) {
        val book = local.getBookById(id) ?: return
        val updated = book.copy(isReading = isReading)
        local.updateBook(updated)
    }
}