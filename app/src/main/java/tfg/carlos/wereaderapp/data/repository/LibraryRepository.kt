package tfg.carlos.wereaderapp.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import tfg.carlos.wereaderapp.data.entity.BookEntity
import tfg.carlos.wereaderapp.data.local.datasource.LibraryLocalDataSource
import tfg.carlos.wereaderapp.data.model.book.BookItem
import tfg.carlos.wereaderapp.data.model.book.toEntity
import tfg.carlos.wereaderapp.data.model.library.LibraryResponse
import tfg.carlos.wereaderapp.data.model.sharedlibrary.SharedLibraryResponse
import tfg.carlos.wereaderapp.data.remote.datasource.LibraryRemoteDadaSource

class LibraryRepository(
    val remote: LibraryRemoteDadaSource,
    val local: LibraryLocalDataSource
) {
    // API Methods
    private suspend fun getAuthUserLibrary() = remote.getAuthUserLibrary()

    private suspend fun getSharedLibrary() = remote.getSharedLibrary()

    // ROOM Methods
    fun getAllBooks(): Flow<List<BookEntity>> = local.getBooksFlow()

    fun getMyBooks(): Flow<List<BookEntity>> = local.getMyBooksFlow()

    fun getSharedBooks(): Flow<List<BookEntity>> = local.getSharedBooksFlow()

    fun getReadingBooks(): Flow<List<BookEntity>> = local.getReadingBooks()

    fun getPendingBooks(): Flow<List<BookEntity>> = local.getPendingBooks()

    suspend fun insertBooks(books: List<BookEntity>) = local.insertBooks(books)

    private suspend fun cacheBooks(bookItems: List<BookEntity>) {
        local.cacheBooks(bookItems)
    }

    suspend fun fetchAndCacheLibrary() {
        val libraryResponse: LibraryResponse = getAuthUserLibrary().first()
        val sharedLibraryResponse: SharedLibraryResponse = getSharedLibrary().first()

        val myBooks = libraryResponse.books.map { it.toEntity(mine = true) }
        val sharedBooks = sharedLibraryResponse.library.books.map { it.toEntity(mine = false) }

        // 💡 Resolver duplicados: priorizar mine = true
        val entities = (myBooks + sharedBooks)
            .groupBy { it.id }
            .mapValues { entry ->
                entry.value.find { it.mine } ?: entry.value.first()
            }
            .values
            .toList()

        Log.d("Repository", "Libros recibidos de API: ${libraryResponse.books.size}")
        Log.d("Repository", "Libros compartidos recibidos de API: ${sharedLibraryResponse.library.books.size}")
        Log.d("Repository", "Entidades generadas (únicas): ${entities.size}")

        cacheBooks(entities)
    }

    suspend fun updateBookReadingStatus(id: String, isReading: Boolean) {
        val book = local.getBookById(id) ?: return
        val updated = book.copy(isReading = isReading)
        local.updateBook(updated)
    }
}