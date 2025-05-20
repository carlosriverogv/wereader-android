package tfg.carlos.wereaderapp.ui.bookDetail

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import tfg.carlos.wereaderapp.ui.reader.ReaderActivity

class BookDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookDetailBinding

    private val storage = Firebase.storage
    private val downloadUrlCache = mutableMapOf<String, String>()

    private val vm: BooksViewModel by viewModels {
        val db = (application as WeReaderApplication).weReaderDB
        val localDataSource = LibraryLocalDataSource(db.bookDao())
        val remoteDataSource = LibraryRemoteDadaSource()
        val repository = LibraryRepository(remoteDataSource, localDataSource)
        BooksViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
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

    // Se carga la renderización del libro
    private fun renderBook() {
        // Se obtiene el ID del libro y si es de la tienda o no del intent
        val bookId = intent.getStringExtra(EXTRA_BOOK_ID) ?: return
        val isStoreBook = intent.getBooleanExtra(EXTRA_IS_STORE_BOOK, false)

        // Se renderiza el libro con liveData
        vm.getBookLiveById(bookId).observe(this) { book ->
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