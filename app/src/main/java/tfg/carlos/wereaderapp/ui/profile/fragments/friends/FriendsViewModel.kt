package tfg.carlos.wereaderapp.ui.profile.fragments.friends

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.data.model.friendship.UserFriendshipsResponse
import tfg.carlos.wereaderapp.data.repository.FriendshipRepository

class FriendsViewModel(val repository: FriendshipRepository) : ViewModel() {
    companion object {
        private val TAG = FriendsViewModel::class.java.simpleName
    }

    private val _friends = MutableLiveData<UserFriendshipsResponse>()
    val friends: LiveData<UserFriendshipsResponse> get() = _friends

    init {
        viewModelScope.launch {
            try {
                val result = repository.getFriendships()
                _friends.postValue(result)
            } catch (e: Exception) {
                // Manejo de errores aquí también
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class FriendsViewModelFactory(private val repository: FriendshipRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FriendsViewModel(repository) as T
    }
}