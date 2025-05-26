package tfg.carlos.wereaderapp.ui.profile.fragments.friends

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.model.friendship.UserFriendshipsResponse
import tfg.carlos.wereaderapp.data.repository.FriendshipRepository
import tfg.carlos.wereaderapp.data.repository.LibraryRepository

class FriendsViewModel(
    private val friendshipRepository: FriendshipRepository,
    private val libraryRepository: LibraryRepository
) : ViewModel() {
    companion object {
        private val TAG = FriendsViewModel::class.java.simpleName
    }

    private val _friends = MutableLiveData<UserFriendshipsResponse>()
    val friends: LiveData<UserFriendshipsResponse> get() = _friends

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    init {
        viewModelScope.launch {
            try {
                val result = friendshipRepository.getFriendships()
                _friends.postValue(result)
            } catch (e: Exception) {
                throw Exception("Error fetching friends: ${e.message}")
            }
        }
    }

    fun shareMyLibraryWithFriend(idFriendUser: String) {
        viewModelScope.launch {
            try {
                libraryRepository.shareMyLibraryWithFriend(idFriendUser)
            } catch (e: Exception) {
                _errorMessage.postValue(e.message ?: "Error inesperado al compartir")
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
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