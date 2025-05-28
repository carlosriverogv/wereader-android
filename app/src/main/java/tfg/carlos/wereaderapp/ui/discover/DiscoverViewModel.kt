package tfg.carlos.wereaderapp.ui.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.data.model.book.BookList
import tfg.carlos.wereaderapp.data.repository.DiscoverRepository

class DiscoverViewModel(val discoverRepository: DiscoverRepository) : ViewModel() {
    companion object {
        private val TAG = DiscoverViewModel::class.java.simpleName
    }

    // LiveData para almacenar los libros más vendidos
    private val _bestsellersBooks = MutableLiveData<BookList>()
    val bestsellersBooks: LiveData<BookList> get() = _bestsellersBooks

    // LiveData para almacenar los nuevos lanzamientos de libros
    private val _newReleasesBooks = MutableLiveData<BookList>()
    val newReleasesBooks: LiveData<BookList> get() = _newReleasesBooks

    // LiveData para manejar mensajes de error
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    init {
        // Cargar los libros más vendidos al iniciar el ViewModel
        loadBestsellersBooks()
        // Cargar los nuevos lanzamientos de libros al iniciar el ViewModel
        loadNewReleasesBooks()
    }

    private fun loadBestsellersBooks() {
        // Cargar los libros de descubrimiento desde el repositorio
        viewModelScope.launch {
            try {
                val result = discoverRepository.getBestsellersBooks()
                _bestsellersBooks.postValue(result)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "Error inesperado al cargar los libros más vendidos")
            }
        }
    }

    /**
     * Carga los nuevos lanzamientos de libros.
     */
    private fun loadNewReleasesBooks() {
        viewModelScope.launch {
            try {
                val result = discoverRepository.getNewReleasesBooks()
                _newReleasesBooks.postValue(result)
            } catch (e: Exception) {
                _errorMessage.postValue(
                    e.message ?: "Error inesperado al cargar los nuevos lanzamientos de libros"
                )
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class DiscoverViewModelFactory(
    private val discoverRepository: DiscoverRepository
)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DiscoverViewModel(discoverRepository) as T
    }
}