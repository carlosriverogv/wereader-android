package tfg.carlos.wereaderapp.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    // Obtiene todos los libros de la base de datos local con flujo
    fun getAllBooks(): Flow<List<BookEntity>> = local.getBooksFlow()

    // Obtiene todos los libros de la base de datos local una sola vez
    suspend fun getAllBooksOnce(): List<BookEntity> = local.getBooksFlow().first()

    // Obtiene un libro por su ID
    suspend fun getBookById(id: String): BookEntity {
        return local.getBookById(id)
    }

    // Obtiene los libros del usuario autenticado con el valor MINE = true
    fun getMyBooks(): Flow<List<BookEntity>> = local.getMyBooksFlow()

    // Obtiene los libros compartidos con el usuario autenticado con el valor MINE = false
    fun getSharedBooks(): Flow<List<BookEntity>> = local.getSharedBooksFlow()

    // Obtiene los libros que están en lectura (isReading = true)
    fun getReadingBooks(): Flow<List<BookEntity>> = local.getReadingBooks()

    // Obtiene los libros que están pendientes de lectura (isPending = true)
    fun getPendingBooks(): Flow<List<BookEntity>> = local.getPendingBooks()

    private suspend fun cacheBooks(bookItems: List<BookEntity>) {
        local.cacheBooks(bookItems)
    }

    /**
     * Función para obtener todos los libros del usuario, almacena ambas bibliotecas
     * en la base de datos local.
     *
     * Con getAuthUserLibrary() se obtiene la biblioteca del usuario autenticado
     * y con getSharedLibrary() se obtiene la biblioteca compartida.
     *
     * A los libros de la biblioteca del usuario autenticado se les asigna el valor mine = true
     * y a los libros de la biblioteca compartida se les asigna el valor mine = false.
     * A ambas bibliotecas se les asigna el id del usuario autenticado para hacer posible el
     * multiusuario en un dispositivo. Al terminar se combinan ambas listas y se eliminan los duplicados
     */
    suspend fun fetchAndCacheLibrary() {
        // Obtener el ID de usuario de la sesión
        val userId = sessionManager.getUserId()

        // Obtener la biblioteca del usuario autenticado y agregar MINE = true y el id del usuario
        val libraryResponse: LibraryResponse = getAuthUserLibrary().first()
        val myBooks = libraryResponse.books.map { it.toEntity(mine = true, idUser = userId) }

        // Se obtiene la biblioteca compartida con el usuario autenticado y agregar MINE = false y el id del usuario
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

    // Actualiza el estado de lectura del libro en la base de datos local
    suspend fun updateBookReadingStatus(id: String, isReading: Boolean) {
        val book = local.getBookById(id)
        val updated = book.copy(isReading = isReading)
        local.updateBook(updated)
    }

    // Actualiza si un libro está pendiente de lectura o no en la base de datos local
    suspend fun updateBookPendingStatus(id: String, isPending: Boolean) {
        val book = local.getBookById(id)
        val updated = book.copy(isPending = isPending)
        local.updateBook(updated)
    }

    /**
     * Actualiza el progreso de lectura del libro en la base de datos local
     * (último localizador y porcentaje de progreso)
     */
    suspend fun updateReadingProgression(id: String, lastLocator: String, progressPercentage: Double) {
        val book = local.getBookById(id)
        val updated = book.copy(lastLocator = lastLocator, readingProgress = progressPercentage)
        local.updateBook(updated)
    }

    /**
     * Devuelve el último localizador del libro
     */
    suspend fun getReadingProgression(id: String): String? {
        val book = local.getBookById(id)
        Log.d("ReaderViewModel", "loadReadingProgression: ${book.id}, lastLocator: ${book.lastLocator}")
        return book.lastLocator
    }

    /**
     * Función para marcar un libro como leído o no leído.
     * Si el libro se marca como leído, se establece el progreso de lectura al 100%
     * y se establece el último localizador a null.
     * Si el libro se marca como no leído, se establece el progreso de lectura al 0%
     * y se establece el último localizador a null.
     */
    suspend fun updateMarkReadOrUnreadBook(id: String, isRead: Boolean) {
        val book = local.getBookById(id)
        val progress = if (isRead) 100.0 else 0.0
        val updated = book.copy(readingProgress = progress, lastLocator = null, isReading = false)
        local.updateBook(updated)
    }
}