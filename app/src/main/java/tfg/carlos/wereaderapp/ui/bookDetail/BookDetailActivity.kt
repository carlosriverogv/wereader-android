package tfg.carlos.wereaderapp.ui.bookDetail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.entity.BookEntity
import tfg.carlos.wereaderapp.data.local.datasource.LibraryLocalDataSource
import tfg.carlos.wereaderapp.data.remote.datasource.LibraryRemoteDadaSource
import tfg.carlos.wereaderapp.data.repository.LibraryRepository
import tfg.carlos.wereaderapp.databinding.ActivityBookDetailBinding
import tfg.carlos.wereaderapp.ui.library.fragments.library.BooksViewModel
import tfg.carlos.wereaderapp.ui.library.fragments.library.BooksViewModelFactory
import tfg.carlos.wereaderapp.ui.library.fragments.sharedlibrary.BookDetailViewModel
import tfg.carlos.wereaderapp.ui.library.fragments.sharedlibrary.BookDetailViewModelFactory
import tfg.carlos.wereaderapp.ui.reader.ReaderActivity
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class BookDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookDetailBinding

    private val storage = Firebase.storage
    private val downloadUrlCache = mutableMapOf<String, String>()

    private var currentBook: BookEntity? = null
    private var optionsMenu: Menu? = null

    /*private val vm: BooksViewModel by viewModels {
        val db = (application as WeReaderApplication).weReaderDB
        val localDataSource = LibraryLocalDataSource(db.bookDao())
        val remoteDataSource = LibraryRemoteDadaSource()
        val repository = LibraryRepository(remoteDataSource, localDataSource)
        BooksViewModelFactory(repository)
    }*/

    private val vm: BookDetailViewModel by viewModels {
        val db = (application as WeReaderApplication).weReaderDB
        val localDataSource = LibraryLocalDataSource(db.bookDao())
        val remoteDataSource = LibraryRemoteDadaSource()
        val repository = LibraryRepository(remoteDataSource, localDataSource)
        BookDetailViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        setSupportActionBar(binding.readerToolbar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Se establece el comportamiento del botón de navegación (botón de retroceso)
        binding.readerToolbar.setNavigationOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

        // Se carga la renderización del libro
        renderBook()
    }

    // Se crea el menú de opciones
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.book_detail_options_menu, menu)
        optionsMenu = menu
        currentBook?.let { setupMenuItems(it) }
        return true
    }

    // Se maneja la selección de opciones del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val book = currentBook ?: return false
        val isStoreBook = intent.getBooleanExtra(EXTRA_IS_STORE_BOOK, false)

        return when (item.itemId) {
            R.id.action_toggle_pending_details -> {
                if (!isStoreBook) {
                    val newPending = !book.isPending
                    vm.updateBookPendingStatus(book.id, newPending)
                    currentBook = book.copy(isPending = newPending)
                    setupMenuItems(currentBook!!)
                }
                Toast.makeText(
                    this,
                    if (book.isPending) {
                        getString(R.string.library_menu_remove_pending_response)
                    } else {
                        getString(R.string.library_menu_add_pending_response)
                    },
                    Toast.LENGTH_SHORT
                ).show()
                true
            }
            R.id.action_mark_read_details -> {
                vm.updateMarkReadOrUnreadBook(book.id, true)
                Toast.makeText(
                    this,
                    getString(R.string.library_menu_mark_read_response),
                    Toast.LENGTH_SHORT
                ).show()
                true
            }
            R.id.action_mark_unread_details -> {
                vm.updateMarkReadOrUnreadBook(book.id, false)
                Toast.makeText(
                    this,
                    getString(R.string.library_menu_mark_unread_response),
                    Toast.LENGTH_SHORT
                ).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupMenuItems(book: BookEntity) {
        val menu = optionsMenu ?: return
        val toggleItem = menu.findItem(R.id.action_toggle_pending_details)
        toggleItem.title = if (book.isPending) {
            getString(R.string.library_menu_remove_pending)
        } else {
            getString(R.string.library_menu_add_pending)
        }
    }

    // Se carga la renderización del libro
    private fun renderBook() {
        // Se obtiene el ID del libro y si es de la tienda o no del intent
        val bookId = intent.getStringExtra(EXTRA_BOOK_ID) ?: return
        val isStoreBook = intent.getBooleanExtra(EXTRA_IS_STORE_BOOK, false)

        // Se renderiza el libro con liveData
        vm.getBookLiveById(bookId).observe(this) { book ->
            currentBook = book
            setupMenuItems(book)
            renderCommonElementsBook(book)
            if (isStoreBook) {
                renderStoreElementsBook(book)
            } else {
                renderUserElementsBook(book)
            }
        }
    }

    override fun onResume() {
        super.onResume()

    }

    // Se abre la actividad de lectura del libro
    private suspend fun openReaderActivity(idBook: String) {
        vm.getBookById(idBook).let { book ->
            val epubPath = book.epubUrl
            val intent = Intent(this@BookDetailActivity, ReaderActivity::class.java)
            intent.putExtra("bookPath", epubPath)
            intent.putExtra("bookId", book.id)
            startActivity(intent)
        }
    }

    // Se renderizan los elementos comunes del libro
    private fun renderCommonElementsBook(book: BookEntity) {
        binding.bookTitle.text = book.title
        binding.bookAuthor.text = book.author
        binding.bookDescription.text = book.synopsis
        binding.bookGenre.text = book.genre

        // Cambiar icono según si el libro es compartible
        val drawableId = if (book.shareable) {
            R.drawable.baseline_check_circle_outline_24 // el que ya tenías
        } else {
            R.drawable.baseline_highlight_off_24 // el icono que muestra "no compartible"
        }

        // Asignar el drawable al TextView (en la parte inferior)
        binding.bookCanShare.setCompoundDrawablesWithIntrinsicBounds(
            null, null,
            null, // izquierda, arriba, derecha
            ContextCompat.getDrawable(binding.root.context, drawableId) // abajo
        )

        val isoString = book.datePublished // "2025-05-05T00:00:00.000Z"
        val inputFormat = DateTimeFormatter.ISO_ZONED_DATE_TIME
        val outputFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        val parsedDate = ZonedDateTime.parse(isoString, inputFormat)
        val formattedDate = parsedDate.format(outputFormat)

        binding.bookDatePublication.text = getString(
            R.string.book_detail_date_publication_label, formattedDate)

        binding.bookIsbn.text = getString(
            R.string.book_detail_isbn_label, book.isbn)

        val cachedUrl = downloadUrlCache[book.coverUrl]
        if (cachedUrl != null) {
            loadImage(cachedUrl)
        } else {
            storage.getReference(book.coverUrl).downloadUrl.addOnSuccessListener { uri ->
                downloadUrlCache[book.coverUrl] = uri.toString()
                loadImage(uri.toString())
            }.addOnFailureListener {
                binding.ivCover.setImageResource(R.drawable.ic_book_placeholder)
            }
        }
    }
    private fun loadImage(url: String) {
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.ic_book_placeholder)
            .transform(FitCenter(), RoundedCorners(8))
            .into(binding.ivCover)
    }


    // Se renderizan los elementos de usuario
    private fun renderUserElementsBook(book: BookEntity) {
        val progress = book.readingProgress.toInt()
        binding.progressPercent.text = binding.progressPercent.context.getString(
            R.string.item_book_progress, progress)
        binding.btnRead.setOnClickListener() {
            vm.updateBookReadingStatus(book.id, true)
            lifecycleScope.launch {
                openReaderActivity(book.id)
            }
        }
    }

    // Se renderizan los elementos de la tienda
    private fun renderStoreElementsBook(book: BookEntity) {
        // todo: lógica para renderizar elementos de la tienda
    }

    companion object {
        const val EXTRA_BOOK_ID = "bookId"
        const val EXTRA_IS_STORE_BOOK = "isStoreBook"
    }
}