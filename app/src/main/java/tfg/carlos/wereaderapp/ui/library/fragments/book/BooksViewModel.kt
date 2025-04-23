package tfg.carlos.wereaderapp.ui.library.fragments.book

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.data.entity.BookEntity
import tfg.carlos.wereaderapp.data.repository.LibraryRepository

class BooksViewModel(val repository: LibraryRepository) : ViewModel() {

    // Libros desde Room (flujo en tiempo real)
    private val _books: Flow<List<BookEntity>> = repository.getLibraryBooks()
    val books: Flow<List<BookEntity>> get() = _books

    /* Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage */

    init {
        // Cargar los libros al iniciar el ViewModel
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            //_isLoading.value = true
            //_errorMessage.value = null
            try {
                val result = repository.fetchAndCacheAuthUserLibrary()
                Log.d("BooksViewModel", "Libros cacheados: ${result.size}")
            } catch (e: Exception) {
                //_errorMessage.value = "Error al cargar libros: ${e.message}"
                Log.e("BooksViewModel", "Error al cargar libros: ${e.message}")
            } //finally {
                //_isLoading.value = false
            //}
        }
    }

    //fun refreshBooks() {
        //loadBooks()
    //}
}

@Suppress("UNCHECKED_CAST")
class BooksViewModelFactory(private val repository: LibraryRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BooksViewModel(repository) as T
    }
}