package tfg.carlos.wereaderapp.ui.profile.fragments.friends

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.model.friendship.UserFriendshipsResponse
import tfg.carlos.wereaderapp.data.model.sharedlibrary.SharedLibrary
import tfg.carlos.wereaderapp.data.repository.FriendshipRepository
import tfg.carlos.wereaderapp.data.repository.LibraryRepository

class FriendsViewModel(
    private val friendshipRepository: FriendshipRepository,
    private val libraryRepository: LibraryRepository
) : ViewModel() {
    companion object {
        private val TAG = FriendsViewModel::class.java.simpleName
    }

    private val sessionManager by lazy {
        WeReaderApplication.sessionManager
    }

    // LiveData para almacenar la lista de amigos del usuario autenticado
    private val _friends = MutableLiveData<UserFriendshipsResponse>()
    val friends: LiveData<UserFriendshipsResponse> get() = _friends

    // LiveData para almacenar la biblioteca compartida por el usuario autenticado
    private val _sharedLibraryByMe = MutableLiveData<SharedLibrary?>()
    val sharedLibraryByMe: LiveData<SharedLibrary?> get() = _sharedLibraryByMe

    // LiveData para manejar mensajes de error
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // LiveData para manejar el éxito de la acción de compartir la biblioteca
    private val _shareSuccess = MutableLiveData<Boolean>()
    val shareSuccess: LiveData<Boolean> get() = _shareSuccess

    init {
        // Cargar la lista de amigos y la biblioteca compartida al iniciar el ViewModel
        loadFriends()
        loadSharedLibraryByMe()
    }


    /**
     * Carga la lista de amigos del usuario autenticado.
     * Se llama al iniciar el ViewModel
     */
    private fun loadFriends() {
        viewModelScope.launch {
            try {
                val result = friendshipRepository.getFriendships()
                _friends.postValue(result)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "Error inesperado al cargar amigos")
            }
        }
    }

    /**
     * Carga la biblioteca compartida por el usuario autenticado.
     * Se llama al iniciar el ViewModel
     *
     * También se guarda el estado de compartir en SharedPreferences.
     * Si no hay biblioteca compartida, se limpia
     */
    private fun loadSharedLibraryByMe() {
        viewModelScope.launch {
            try {
                val sharedLibrary = libraryRepository.getSharedLibraryByMe()
                _sharedLibraryByMe.postValue(sharedLibrary)

                if (sharedLibrary != null) {
                    sessionManager.saveSharingLibrary(
                        isSharing = true,
                        friendUserId = sharedLibrary.idUserFriend
                    )
                } else {
                    sessionManager.clearSharingLibrary()
                }
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "Error inesperado al cargar biblioteca compartida")
            }
        }
    }

    /**
     * Comparte la biblioteca del usuario autenticado con un amigo.
     * @param idFriendUser El ID del usuario amigo con quien se desea compartir la biblioteca.
     */
    fun shareMyLibraryWithFriend(idFriendUser: String) {
        viewModelScope.launch {
            try {
                // Se limpia la compartición anterior si existe
                clearShareSuccess()
                // Se comparte la biblioteca con el amigo
                libraryRepository.shareMyLibraryWithFriend(idFriendUser)
                // Se sobreescribe la comprobación de éxito
                _shareSuccess.postValue(true)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "Error inesperado al compartir")
                _shareSuccess.postValue(false)
            }
        }
    }

    /**
     * Elimina la biblioteca compartida por el usuario autenticado.
     * @param idFriendUser El ID del usuario amigo con quien se ha compartido la biblioteca.
     */
    fun stopSharingMyLibrary(idFriendUser: String) {
        viewModelScope.launch {
            try {
                libraryRepository.stopSharingMyLibrary(idFriendUser)
                _sharedLibraryByMe.postValue(null)

            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "Error inesperado al eliminar la biblioteca compartida")
            }
        }
    }

    /**
     * Elimina una amistad del usuario autenticado.
     * @param idFriendUser El ID del usuario amigo a eliminar.
     */
    fun deleteMyFriendship(idFriendUser: String) {
        viewModelScope.launch {
            try {
                friendshipRepository.deleteMyFriendship(idFriendUser)
                // Recargar la lista de amigos después de eliminar
                loadFriends()
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "Error inesperado al eliminar la amistad")
            }
        }
    }


    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    private fun clearShareSuccess() {
        _shareSuccess.value = false
    }
}

@Suppress("UNCHECKED_CAST")
class FriendsViewModelFactory(
    private val friendshipRepository: FriendshipRepository,
    private val libraryRepository: LibraryRepository
)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FriendsViewModel(friendshipRepository, libraryRepository) as T
    }
}