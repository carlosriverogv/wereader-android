package tfg.carlos.wereaderapp.ui.profile.fragments.friendrequests

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.data.model.friendship.UserFriendshipsResponse
import tfg.carlos.wereaderapp.data.repository.FriendshipRepository

class FriendRequestsViewModel(
    private val friendshipRepository: FriendshipRepository,
): ViewModel()  {
    companion object {
        private val TAG = FriendRequestsViewModel::class.java.simpleName
    }

    // LiveData para almacenar las solicitudes de amistad del usuario autenticado
    private val _friendRequests = MutableLiveData<UserFriendshipsResponse>()
    val friendRequests: LiveData<UserFriendshipsResponse> get() = _friendRequests

    // LiveData para manejar mensajes de error
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    init {
        loadFriendRequests()
    }

    /**
     * Carga las solicitudes de amistad del usuario autenticado.
     * Se llama al iniciar el ViewModel.
     */
    private fun loadFriendRequests() {
        viewModelScope.launch {
            try {
                val result = friendshipRepository.getReceivedFriendshipRequests()
                _friendRequests.postValue(result)
            } catch (e: Exception) {
                // Manejo de errores, se puede registrar o mostrar un mensaje al usuario
                _errorMessage.postValue(
                    e.message ?: "Error inesperado al cargar solicitudes de amistad")
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class FriendRequestsViewModelFactory(
    private val friendshipRepository: FriendshipRepository,
)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FriendRequestsViewModel(friendshipRepository) as T
    }
}