package tfg.carlos.wereaderapp.ui.library.fragments.library

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.entity.BookEntity
import tfg.carlos.wereaderapp.data.repository.LibraryRepository

class LibraryViewModel(val repository: LibraryRepository) : ViewModel() {
    companion object {
        private val TAG = LibraryViewModel::class.java.simpleName
    }
    // Flujo para obtener todos los libros del usuario autenticado (MINE = true)
    private val _myBooks: Flow<List<BookEntity>> = repository.getMyBooks()
    val myBooks: Flow<List<BookEntity>> get() = _myBooks

    // Flujo para obtener los libros compartidos (MINE = false)
    private val _sharedBooks: Flow<List<BookEntity>> = repository.getSharedBooks()
    val sharedBooks: Flow<List<BookEntity>> get() = _sharedBooks

    // Cargar los libros al iniciar el ViewModel
    init {
        loadBooks()
    }

    // Función para cargar los libros desde la API y almacenarlos en la base de datos local
    private fun loadBooks() {
        viewModelScope.launch {
            try {
                repository.fetchAndCacheLibrary()
            } catch (e: Exception) {
                showToastError(R.string.error_loading_books, e.message)
            }
        }
    }

    // Función para obtener un libro por su ID
    suspend fun getBookById(id: String): BookEntity {
        return repository.getBookById(id)
    }

    // Función para añadir/eliminar un libro de pendientes
    fun updateBookPendingStatus(id: String, isPending: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateBookPendingStatus(id, isPending)
            } catch (e: Exception) {
                showToastError(R.string.error_updating_pending_status, e.message)
            }
        }
    }

    // Función para actualizar el estado de lectura de un libro
    fun updateBookReadingStatus(id: String, isReading: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateBookReadingStatus(id, isReading)
            } catch (e: Exception) {
                showToastError(R.string.error_updating_reading_status, e.message)
            }
        }
    }

    // Función para actualizar el estado de lectura de un libro (marcar como leído o no leído)
    fun updateMarkReadOrUnreadBook(id: String, isRead: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateMarkReadOrUnreadBook(id, isRead)
            } catch (e: Exception) {
                showToastError(R.string.error_updating_mark_status, e.message)
            }
        }
    }

    // Mostrar un mensaje de error en caso de que falle alguna operación
    private fun showToastError(stringResId: Int, detail: String?) {
        val context = WeReaderApplication.instance
        val message = context.getString(stringResId) + (detail ?: "")
        Log.e(TAG, message)
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

@Suppress("UNCHECKED_CAST")
class BooksViewModelFactory(private val repository: LibraryRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LibraryViewModel(repository) as T
    }
}