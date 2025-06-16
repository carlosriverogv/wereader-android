package tfg.carlos.wereaderapp.ui.bookDetail

 import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
 import androidx.lifecycle.MutableLiveData
 import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.entity.BookEntity
 import tfg.carlos.wereaderapp.data.model.book.BookItem
 import tfg.carlos.wereaderapp.data.repository.BookRepository
 import tfg.carlos.wereaderapp.data.repository.LibraryRepository

class BookDetailViewModel(
    private val libraryRepository: LibraryRepository,
    private val bookRepository: BookRepository
) : ViewModel() {
    companion object {
        private val TAG = BookDetailViewModel::class.java.simpleName
    }

    // LiveData para manejar mensajes de error
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // LiveData para manejar el éxito de la acción de compartir la biblioteca
    private val _buySuccess = MutableLiveData<Boolean>()
    val buySuccess: LiveData<Boolean> get() = _buySuccess

    // API Methods
    suspend fun getStoreBookById(bookId: String): BookItem? {
        return bookRepository.getStoreBookById(bookId)
    }

    // Función para comprar un libro
    fun buyBook(bookId: String) {
        viewModelScope.launch {
            try {
                libraryRepository.buyBook(bookId)
                // Si la compra es exitosa, puedes actualizar el estado del libro o notificar al usuario
                _buySuccess.postValue(true)
            } catch (e: Exception) {
                //showToastError(R.string.error_buying_book, e.message)
                _errorMessage.postValue(e.message)
            }
        }
    }

    // ROOM Methods
    // Función para obtener un libro por su ID con LiveData (OBTIENE DE ROOM)
    fun getBookLiveById(id: String): LiveData<BookEntity> {
        return libraryRepository.getBookLiveById(id)
    }

    // Función para obtener un libro por su ID
    suspend fun getBookById(id: String): BookEntity {
        return libraryRepository.getBookById(id)
    }

    // Función para añadir/eliminar un libro de pendientes
    fun updateBookPendingStatus(id: String, isPending: Boolean) {
        viewModelScope.launch {
            try {
                libraryRepository.updateBookPendingStatus(id, isPending)
            } catch (e: Exception) {
                showToastError(R.string.error_updating_pending_status, e.message)
            }
        }
    }

    // Función para actualizar el estado de lectura de un libro
    fun updateBookReadingStatus(id: String, isReading: Boolean) {
        viewModelScope.launch {
            try {
                libraryRepository.updateBookReadingStatus(id, isReading)
            } catch (e: Exception) {
                showToastError(R.string.error_updating_reading_status, e.message)
            }
        }
    }

    // Función para actualizar el estado de lectura de un libro (marcar como leído o no leído)
    fun updateMarkReadOrUnreadBook(id: String, isRead: Boolean) {
        viewModelScope.launch {
            try {
                libraryRepository.updateMarkReadOrUnreadBook(id, isRead)
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

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    private fun clearShareSuccess() {
        _buySuccess.value = false
    }
}

@Suppress("UNCHECKED_CAST")
class BookDetailViewModelFactory(
    private val libraryRepository: LibraryRepository,
    private val bookRepository: BookRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BookDetailViewModel(libraryRepository, bookRepository) as T
    }
}