package tfg.carlos.wereaderapp.ui.library.fragments.library

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.carlosrivero.demo5.utils.checkConnection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.data.entity.BookEntity
import tfg.carlos.wereaderapp.data.repository.LibraryRepository

class BooksViewModel(val repository: LibraryRepository) : ViewModel() {

    // Libros desde Room (flujo en tiempo real)
    private val _myBooks: Flow<List<BookEntity>> = repository.getMyBooks()
    val myBooks: Flow<List<BookEntity>> get() = _myBooks

    private val _sharedBooks: Flow<List<BookEntity>> = repository.getSharedBooks()
    val sharedBooks: Flow<List<BookEntity>> get() = _sharedBooks

    init {
        // Cargar los libros al iniciar el ViewModel
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            try {
                repository.fetchAndCacheLibrary()
            } catch (e: Exception) {
                Log.e("BooksViewModel", "Error al cargar libros: ${e.message}")
            }
        }
    }

    fun updateBookReadingStatus(id: String, isReading: Boolean) {
        viewModelScope.launch {
            repository.updateBookReadingStatus(id, isReading)

            //TODO: Se actualiza el progreso de lectura aqui
        }
    }

    fun updateBookPendingStatus(id: String, isPending: Boolean) {
        viewModelScope.launch {
            repository.updateBookPendingStatus(id, isPending)
        }
    }

    fun updateBookReadingProgress(id: String, progress: Double) {
        viewModelScope.launch {
            repository.updateBookReadingProgress(id, progress)
        }
    }

    suspend fun getBookById(id: String): BookEntity {
        return repository.getBookById(id)
    }
}

@Suppress("UNCHECKED_CAST")
class BooksViewModelFactory(private val repository: LibraryRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BooksViewModel(repository) as T
    }
}