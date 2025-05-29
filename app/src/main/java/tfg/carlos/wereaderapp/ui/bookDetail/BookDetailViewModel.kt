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

    // API Methods
    suspend fun getStoreBookById(bookId: String): BookItem? {
        return bookRepository.getStoreBookById(bookId)
    }

    // Función para comprar un libro
    fun buyBook(bookId: String) {
        viewModelScope.launch {
            try {
                libraryRepository.buyBook(bookId)
            } catch (e: Exception) {
                showToastError(R.string.error_buying_book, e.message)
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