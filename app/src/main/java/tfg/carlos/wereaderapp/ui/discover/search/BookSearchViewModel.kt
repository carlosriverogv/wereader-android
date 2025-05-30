package tfg.carlos.wereaderapp.ui.discover.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.data.model.book.BookList
import tfg.carlos.wereaderapp.data.model.user.UserListResponse
import tfg.carlos.wereaderapp.data.repository.BookRepository
import tfg.carlos.wereaderapp.data.repository.FriendshipRepository
import tfg.carlos.wereaderapp.data.repository.UserRepository
import tfg.carlos.wereaderapp.ui.profile.fragments.addfriend.AddFriendViewModel

class BookSearchViewModel(
    private val bookRepository: BookRepository
) : ViewModel() {
    private val _searchResults = MutableLiveData<BookList>()
    val searchResults: LiveData<BookList> get() = _searchResults

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(400)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isNotBlank()) {
                        searchBook(query)
                    }
                }
        }
    }

    /**
     * Busca libros por una consulta.
     * @param query Consulta a buscar.
     */
    private fun searchBook(query: String) {
        viewModelScope.launch {
            try {
                val results: BookList = bookRepository.searchBooks(query)
                _searchResults.postValue(results)
                _errorMessage.postValue(null)
            } catch (e: Exception) {
                _searchResults.postValue(BookList())
                _errorMessage.postValue(e.message ?: "Error inesperado al buscar libros")
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}

@Suppress("UNCHECKED_CAST")
class BookSearchViewModelFactory(
    private val bookRepository: BookRepository,
)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BookSearchViewModel(bookRepository) as T
    }
}
