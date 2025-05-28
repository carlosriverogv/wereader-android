package tfg.carlos.wereaderapp.ui.profile.fragments.addfriend

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
import tfg.carlos.wereaderapp.data.model.user.UserListResponse
import tfg.carlos.wereaderapp.data.repository.FriendshipRepository
import tfg.carlos.wereaderapp.data.repository.UserRepository

class AddFriendViewModel(
    private val userRepository: UserRepository,
    private val friendshipRepository: FriendshipRepository
) : ViewModel() {
    companion object {
        private val TAG = AddFriendViewModel::class.java.simpleName
    }

    private val _searchResults = MutableLiveData<UserListResponse>()
    val searchResults: LiveData<UserListResponse> get() = _searchResults

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // LiveData para manejar el éxito de añadir un amigo
    private val _shareSuccess = MutableLiveData<Boolean>()
    val shareSuccess: LiveData<Boolean> get() = _shareSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(200)
                .distinctUntilChanged()
                .collect { tag ->
                    if (tag.isNotBlank()) {
                        searchUserByTag(tag)
                    }
                }
        }
    }

    /**
     * Busca usuarios por etiqueta (tag).
     * @param tag Etiqueta a buscar.
     */
    private fun searchUserByTag(tag: String) {
        viewModelScope.launch {
            try {
                val result: UserListResponse = userRepository.searchUserByTag(tag)
                _searchResults.postValue(result)
                _errorMessage.postValue(null) // limpiar error anterior si hubo
            } catch (e: Exception) {
                _searchResults.postValue(UserListResponse())
                _errorMessage.postValue(e.message ?: "Error inesperado al buscar usuario")
            }
        }
    }

    /**
     * Crea una solicitud de amistad para el usuario autenticado.
     * @param idFriendUser ID del usuario amigo al que se le envía la solicitud.
     */
    fun createFriendship(idFriendUser: String) {
        viewModelScope.launch {
            try {
                friendshipRepository.createFriendship(idFriendUser)
                _errorMessage.postValue(null) // limpiar error anterior si hubo
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "Error al crear la solicitud de amistad")
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}

@Suppress("UNCHECKED_CAST")
class AddFriendViewModelFactory(
    private val userRepository: UserRepository,
    private val friendshipRepository: FriendshipRepository
)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddFriendViewModel(userRepository, friendshipRepository) as T
    }
}