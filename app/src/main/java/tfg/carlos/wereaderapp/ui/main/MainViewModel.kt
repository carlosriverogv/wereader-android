package tfg.carlos.wereaderapp.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.data.entity.BookEntity
import tfg.carlos.wereaderapp.data.repository.LibraryRepository

class MainViewModel(val repository: LibraryRepository): ViewModel() {
    private val _readingBooks: Flow<List<BookEntity>> = repository.getReadingBooks()
    val readingBooks: Flow<List<BookEntity>> get() = _readingBooks

    private val _pendingBooks: Flow<List<BookEntity>> = repository.getPendingBooks()
    val pendingBooks: Flow<List<BookEntity>> get() = _pendingBooks

    init {
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
class MainViewModelFactory(private val repository: LibraryRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}