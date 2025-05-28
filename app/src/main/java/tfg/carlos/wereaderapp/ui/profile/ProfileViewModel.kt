package tfg.carlos.wereaderapp.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.data.model.user.User
import tfg.carlos.wereaderapp.data.repository.LibraryRepository
import tfg.carlos.wereaderapp.data.repository.UserRepository

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    init {
        getProfileUser()
    }

    /**
     * Obtiene el perfil del usuario actual.
     * Se llama al inicio para cargar los datos del usuario.
     */
    private fun getProfileUser() {
        viewModelScope.launch {
            try {
                val result = userRepository.getUserProfile()
                _user.postValue(result)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error obteniendo el usuario", e)
            }
        }
    }

    fun getPendingBooksCount(): LiveData<Int> {
        val pendingBooksCount = MutableLiveData<Int>()
        viewModelScope.launch {
            try {
                val count = libraryRepository.getPendingBooksCount()
                pendingBooksCount.postValue(count)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error obteniendo el conteo de libros pendientes", e)
                pendingBooksCount.postValue(0) // En caso de error, devolvemos 0
            }
        }
        return pendingBooksCount
    }

    fun getTotalBooksCount(): LiveData<Int> {
        val totalBooksCount = MutableLiveData<Int>()
        viewModelScope.launch {
            try {
                val count = libraryRepository.getTotalBooksCount()
                totalBooksCount.postValue(count)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error obteniendo el conteo de libros totales", e)
                totalBooksCount.postValue(0) // En caso de error, devolvemos 0
            }
        }
        return totalBooksCount
    }

    fun getFinishedBooksCount(): LiveData<Int> {
        val finishedBooksCount = MutableLiveData<Int>()
        viewModelScope.launch {
            try {
                val count = libraryRepository.getFinishedBooksCount()
                finishedBooksCount.postValue(count)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error obteniendo el conteo de libros finalizados", e)
                finishedBooksCount.postValue(0) // En caso de error, devolvemos 0
            }
        }
        return finishedBooksCount
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
class ProfileViewModelFactory(
    private val userRepository: UserRepository,
    private val libraryRepository: LibraryRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(userRepository, libraryRepository) as T
    }
}