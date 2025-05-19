package tfg.carlos.wereaderapp.ui.library.fragments.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.data.entity.BookEntity
import tfg.carlos.wereaderapp.data.repository.LibraryRepository

class BooksViewModel(val repository: LibraryRepository) : ViewModel() {
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
                throw Exception("Error al cargar libros: ${e.message}")
            }
        }
    }

    // Función para obtener un libro por su ID
    suspend fun getBookById(id: String): BookEntity {
        return repository.getBookById(id)
    }

    // Función para actualizar el estado de lectura de un libro
    fun updateBookReadingStatus(id: String, isReading: Boolean) {
        viewModelScope.launch {
            repository.updateBookReadingStatus(id, isReading)
        }
    }

    // Función para actualizar el estado de pendiente de un libro
    fun updateBookPendingStatus(id: String, isPending: Boolean) {
        viewModelScope.launch {
            repository.updateBookPendingStatus(id, isPending)
        }
    }

    // Función para marcar un libro como leído o no leído
    fun updateMarkReadOrUnreadBook(id: String, isRead: Boolean) {
        viewModelScope.launch {
            repository.updateMarkReadOrUnreadBook(id, isRead)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class BooksViewModelFactory(private val repository: LibraryRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BooksViewModel(repository) as T
    }
}