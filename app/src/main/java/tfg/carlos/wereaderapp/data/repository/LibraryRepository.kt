package tfg.carlos.wereaderapp.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.readium.r2.shared.publication.Locator
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.entity.BookEntity
import tfg.carlos.wereaderapp.data.local.datasource.LibraryLocalDataSource
import tfg.carlos.wereaderapp.data.model.book.toEntity
import tfg.carlos.wereaderapp.data.model.library.LibraryResponse
import tfg.carlos.wereaderapp.data.remote.datasource.LibraryRemoteDadaSource

class LibraryRepository(
    val remote: LibraryRemoteDadaSource,
    val local: LibraryLocalDataSource

) {
    private val sessionManager by lazy {
        WeReaderApplication.sessionManager
    }

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
        // Obtener el ID de usuario de la sesión
        val userId = sessionManager.getUserId()

        // Obtener la biblioteca del usuario autenticado
        val libraryResponse: LibraryResponse = getAuthUserLibrary().first()
        val myBooks = libraryResponse.books.map { it.toEntity(mine = true, idUser = userId) }

        // Se obtiene la biblioteca compartida con el usuario autenticado
        // Manejar el caso en que no hay biblioteca compartida (404)
        val sharedBooks = try {
            val sharedLibraryResponse = getSharedLibrary().first()
            sharedLibraryResponse.library.books.map { it.toEntity(mine = false, idUser = userId) }
        } catch (e: Exception) {
            emptyList()
        }

        // Combinar libros propios y compartidos, asegurando que no haya duplicados
        val entities = (myBooks + sharedBooks)
            .groupBy { it.id }
            .mapValues { entry ->
                entry.value.find { it.mine } ?: entry.value.first()
            }
            .values
            .toList()

        cacheBooks(entities)
    }

    suspend fun updateBookReadingStatus(id: String, isReading: Boolean) {
        val book = local.getBookById(id)
        val updated = book.copy(isReading = isReading)
        local.updateBook(updated)
    }

    suspend fun updateBookPendingStatus(id: String, isPending: Boolean) {
        val book = local.getBookById(id)
        val updated = book.copy(isPending = isPending)
        local.updateBook(updated)
    }

    suspend fun updateReadingProgression(id: String, lastLocator: String) {
        val book = local.getBookById(id)
        val updated = book.copy(lastLocator = lastLocator)
        local.updateBook(updated)
    }

    suspend fun getReadingProgression(id: String): String? {
        val book = local.getBookById(id)
        Log.d("ReaderViewModel", "Book loaded Repo: ${book.id}, lastLocator: ${book.lastLocator}")
        return book.lastLocator
    }
}