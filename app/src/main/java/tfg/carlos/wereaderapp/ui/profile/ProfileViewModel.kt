package tfg.carlos.wereaderapp.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.data.model.user.User
import tfg.carlos.wereaderapp.data.repository.AuthRepository

class ProfileViewModel(val repository: AuthRepository) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    init {
        getUser()
    }

    private fun getUser() {
        viewModelScope.launch {
            try {
                val result = repository.getUser()
                _user.postValue(result)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error obteniendo el usuario", e)
            }
        }
    }

    /*fun updateAvatar(avatarId: Int) {
        viewModelScope.launch {
            repository.updateAvatar(avatarId)
        }
    }

    fun logout() {

    }*/
}

@Suppress("UNCHECKED_CAST")
class ProfileViewModelFactory(private val repository: AuthRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(repository) as T
    }
}