package tfg.carlos.wereaderapp.data.local.datasource

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.entity.BookEntity
import tfg.carlos.wereaderapp.data.local.interfaces.BookDao

class LibraryLocalDataSource(private val dao: BookDao) {
    private val sessionManager by lazy {
        WeReaderApplication.sessionManager
    }

    fun getBooksFlow(): Flow<List<BookEntity>> = dao.getAllBooks()
    private suspend fun getBooksOnce(): List<BookEntity> = dao.getAllBooksOnce()
    fun getMyBooksFlow(): Flow<List<BookEntity>> = dao.getMyBooks(getIdUser())
    fun getSharedBooksFlow(): Flow<List<BookEntity>> = dao.getSharedBooks(getIdUser())

    suspend fun getBookById(id: String): BookEntity {
        return dao.getBookById(id, getIdUser())
    }

    fun getBookLiveById(id: String): LiveData<BookEntity> {
        return dao.getBookLiveById(id, getIdUser())
    }

    suspend fun insertBooks(books: List<BookEntity>) {
        dao.insertAll(books)
    }

    /**
     * Función para cachear los libros de la base de datos local.
     * Se eliminan los libros existentes y se insertan los nuevos.
     * Se utiliza para almacenar la biblioteca del usuario autenticado y la biblioteca compartida.
     *
     * Para hacer el cacheo sin perder datos como (lastLocator, isReading o isPending),
     * se hace un merge, copiando estos campos de los libros existentes a los que entran de la API.
     */
    suspend fun cacheBooks(newBooks: List<BookEntity>) {
        val existingBooks = getBooksOnce()

        // Merge de campos locales
        val mergedBooks = newBooks.map { newBook ->
            val old = existingBooks.find { it.id == newBook.id && it.idUser == newBook.idUser }
            newBook.copy(
                isReading = old?.isReading ?: false,
                isPending = old?.isPending ?: false,
                readingProgress = old?.readingProgress ?: 0.0,
                lastLocator = old?.lastLocator, // Se evita  sobreescribir el último localizador al cachear
                mine = newBook.mine,
                idUser = newBook.idUser
            )
        }

        // Ya no se borra toda la tabla
        dao.insertAll(mergedBooks)
    }

    // Función general para actualizar un libro
    suspend fun updateBook(book: BookEntity) {
        dao.updateBook(book)
    }

    // Obtiene los libros que están en lectura (isReading = true)
    fun getPendingBooks(): Flow<List<BookEntity>> {
        return dao.getPendingBooks(getIdUser())
    }

    // Obtiene los libros que están pendientes de lectura (isPending = true)
    fun getReadingBooks(): Flow<List<BookEntity>> {
        return dao.getReadingBooks(getIdUser())
    }

    // TODO: Se puede mover a UserDataSource
    // Obtiene el ID de usuario de la sesión
    private fun getIdUser(): String {
        return sessionManager.getUserId()
    }
}