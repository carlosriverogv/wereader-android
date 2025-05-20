package tfg.carlos.wereaderapp.ui.main

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.entity.BookEntity
import tfg.carlos.wereaderapp.data.repository.LibraryRepository

class MainViewModel(val repository: LibraryRepository): ViewModel() {
    // Flujo para obtener los libros en estado de lectura (isReading = true)
    private val _readingBooks: Flow<List<BookEntity>> = repository.getReadingBooks()
    val readingBooks: Flow<List<BookEntity>> get() = _readingBooks

    // Flujo para obtener los libros pendientes (isPending = true)
    private val _pendingBooks: Flow<List<BookEntity>> = repository.getPendingBooks()
    val pendingBooks: Flow<List<BookEntity>> get() = _pendingBooks

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
                Log.e("MainViewModel", "Error loading books: ${e.message}")
                Toast.makeText(
                    WeReaderApplication.instance,
                    "Error loading books: ${e.message}",
                    Toast.LENGTH_LONG).show()
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
class MainViewModelFactory(private val repository: LibraryRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}