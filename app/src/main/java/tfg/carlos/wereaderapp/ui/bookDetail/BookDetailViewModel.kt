package tfg.carlos.wereaderapp.ui.bookDetail

 import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.entity.BookEntity
import tfg.carlos.wereaderapp.data.repository.LibraryRepository

class BookDetailViewModel(private val repository: LibraryRepository) : ViewModel() {
    companion object {
        private val TAG = BookDetailViewModel::class.java.simpleName
    }

    // Función para obtener un libro por su ID con LiveData
    fun getBookLiveById(id: String): LiveData<BookEntity> {
        return repository.getBookLiveById(id)
    }

    // Función para obtener un libro por su ID
    suspend fun getBookById(id: String): BookEntity {
        return repository.getBookById(id)
    }

    // Función para añadir/eliminar un libro de pendientes
    fun updateBookPendingStatus(id: String, isPending: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateBookPendingStatus(id, isPending)
            } catch (e: Exception) {
                showToastError(R.string.error_updating_pending_status, e.message)
            }
        }
    }

    // Función para actualizar el estado de lectura de un libro
    fun updateBookReadingStatus(id: String, isReading: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateBookReadingStatus(id, isReading)
            } catch (e: Exception) {
                showToastError(R.string.error_updating_reading_status, e.message)
            }
        }
    }

    // Función para actualizar el estado de lectura de un libro (marcar como leído o no leído)
    fun updateMarkReadOrUnreadBook(id: String, isRead: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateMarkReadOrUnreadBook(id, isRead)
            } catch (e: Exception) {
                showToastError(R.string.error_updating_mark_status, e.message)
            }
        }
    }

    // Mostrar un mensaje de error en caso de que falle alguna operación
    private fun showToastError(stringResId: Int, detail: String?) {
        val context = WeReaderApplication.instance
        val message = context.getString(stringResId) + (detail ?: "")
        Log.e(TAG, message)
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

@Suppress("UNCHECKED_CAST")
class BookDetailViewModelFactory(private val repository: LibraryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BookDetailViewModel(repository) as T
    }
}