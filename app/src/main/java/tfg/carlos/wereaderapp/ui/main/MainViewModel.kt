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
    private val _books: Flow<List<BookEntity>> = repository.getReadingBooks()
    val books: Flow<List<BookEntity>> get() = _books

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
}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(private val repository: LibraryRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}