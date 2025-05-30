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
import edu.carlosrivero.demo5.utils.isTokenValid
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.entity.BookEntity
import tfg.carlos.wereaderapp.data.local.datasource.LibraryLocalDataSource
import tfg.carlos.wereaderapp.data.model.book.BookItem
import tfg.carlos.wereaderapp.data.model.book.toEntity
import tfg.carlos.wereaderapp.data.remote.datasource.BookRemoteDataSource
import tfg.carlos.wereaderapp.data.remote.datasource.LibraryRemoteDadaSource
import tfg.carlos.wereaderapp.data.repository.BookRepository
import tfg.carlos.wereaderapp.data.repository.LibraryRepository
import tfg.carlos.wereaderapp.databinding.ActivityBookDetailBinding
import tfg.carlos.wereaderapp.ui.auth.login.LoginActivity
import tfg.carlos.wereaderapp.ui.reader.ReaderActivity
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class BookDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookDetailBinding

    private val sessionManager by lazy {
        WeReaderApplication.sessionManager
    }

    private val storage = Firebase.storage
    private val downloadUrlCache = mutableMapOf<String, String>()

    private var currentBook: BookEntity? = null
    private var optionsMenu: Menu? = null

    private var isStoreBook: Boolean = false

    private val vm: BookDetailViewModel by viewModels {
        val db = (application as WeReaderApplication).weReaderDB
        val localDataSource = LibraryLocalDataSource(db.bookDao())
        val remoteDataSource = LibraryRemoteDadaSource()
        val libraryRepository = LibraryRepository(remoteDataSource, localDataSource)

        val bookRemoteDataSource = BookRemoteDataSource()
        val bookRepository = BookRepository(bookRemoteDataSource)
        BookDetailViewModelFactory(libraryRepository, bookRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val token = sessionManager.getToken()

        if (isTokenValid(token)) {
            setupIU()
        }
    }

    override fun onResume() {
        super.onResume()
        val token = sessionManager.getToken()

        if (!isTokenValid(token)) {
            goToLogin()
        }
    }

    private fun setupIU() {
        // Se inicializa la vista
        binding = ActivityBookDetailBinding.inflate(layoutInflater)

        // Se habilita el modo Edge to Edge
        enableEdgeToEdge()
        setContentView(binding.root)
        setSupportActionBar(binding.readerToolbar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        isStoreBook = intent.getBooleanExtra(EXTRA_IS_STORE_BOOK, false)

        // Se establece el comportamiento del botón de navegación (botón de retroceso)
        binding.readerToolbar.setNavigationOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

        // Se carga la renderización del libro
        renderBook()
    }

    /*
     * Se crea el menú de opciones de la actividad.
     * Si el libro es de la tienda, no se muestran las opciones de menú.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Si el libro es de la tienda, no se muestran las opciones de menú
        if (isStoreBook) return false

        menuInflater.inflate(R.menu.book_detail_options_menu, menu)
        optionsMenu = menu
        currentBook?.let { setupMenuItems(it) }
        return true
    }

    /*
     * Se maneja la selección de los elementos del menú.
     */
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

    /**
     * Configura los elementos del menú según el estado del libro.
     * Cambia el título del elemento de menú para agregar o eliminar de pendientes.
     */
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

        // Si es un libro de la tienda, se obtiene el libro de la API
        if (isStoreBook) {
            lifecycleScope.launch {
                try {
                    val bookItem = vm.getStoreBookById(bookId) ?: run {
                        Toast.makeText(this@BookDetailActivity,
                            "Libro no encontrado", Toast.LENGTH_LONG).show()
                        finish() // Cerrar la actividad si no se encuentra el libro
                        return@launch
                    }

                    // Convertir BookItem a BookEntity para usar lógica común
                    val bookAsEntity = bookItem.toEntity(mine = false, idUser = "")

                    // Reutilizar renderizado común
                    renderCommonElementsBook(bookAsEntity)
                    renderStoreElementsBook(bookAsEntity)

                } catch (e: Exception) {
                    throw Exception("Error al obtener el libro de la tienda: ${e.message}", e)
                }
            }
        } else {
            // Si no es un libro de la tienda, se obtiene el libro de la base de datos local
            vm.getBookLiveById(bookId).observe(this) { book ->
                currentBook = book
                renderCommonElementsBook(book)
                setupMenuItems(book)
                renderUserElementsBook(book)
            }
        }
    }

    /**
     * Abre la actividad de lectura del libro con el ID proporcionado.
     * Se obtiene la URL del libro y se inicia la actividad de lectura.
     */
    private suspend fun openReaderActivity(idBook: String) {
        vm.getBookById(idBook).let { book ->
            val epubPath = book.epubUrl
            val intent = Intent(this@BookDetailActivity, ReaderActivity::class.java)
            intent.putExtra("bookPath", epubPath)
            intent.putExtra("bookId", book.id)
            startActivity(intent)
        }
    }

    /**
     * Renderiza los elementos comunes del libro, como título, autor, descripción, género,
     * icono de compartibilidad y fecha de publicación.
     */
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

    /*
     * Carga la imagen de la portada del libro utilizando Glide.
     * Se aplica un placeholder y transformaciones para el ajuste y esquinas redondeadas.
     */
    private fun loadImage(url: String) {
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.ic_book_placeholder)
            .transform(FitCenter(), RoundedCorners(8))
            .into(binding.ivCover)
    }


    /*
     * Renderiza los elementos específicos del usuario para el libro.
     * Muestra el progreso de lectura y el botón de leer.
     */
    private fun renderUserElementsBook(book: BookEntity) {
        // Se carga el progreso de lectura del libro
        val progress = book.readingProgress.toInt()
        binding.progressPrice.text = binding.progressPrice.context.getString(
            R.string.book_detail_progress, progress)

        // Cuando se pulsa el botón de leer,
        // se actualiza el estado del libro a leyendo y se abre la actividad de lectura
        binding.btnRead.setOnClickListener() {
            vm.updateBookReadingStatus(book.id, true)
            lifecycleScope.launch {
                openReaderActivity(book.id)
            }
        }
    }

    /**
     * Renderiza los elementos específicos de la tienda para el libro.
     * Muestra el precio y el botón de compra.
     */
    private fun renderStoreElementsBook(book: BookEntity) {
        // Se carga el precio del libro
        val price = book.price
        binding.progressPrice.text = binding.progressPrice.context.getString(
            R.string.book_detail_price, price)

        binding.btnRead.text = getString(R.string.book_detail_buy_button)
        binding.btnRead.setOnClickListener {
            // COMPRAR EL LIBRO
            buyBook(book)
        }
    }

    /**
     *  Función para realizar la compra del libro.
     */
    private fun buyBook(book: BookEntity) {
        lifecycleScope.launch {
            try {
                // Se añade el libro a la biblioteca del usuario autenticado
                vm.buyBook(book.id)

                // Se muestra un mensaje de éxito
                Toast.makeText(
                    this@BookDetailActivity,
                    "Compra realizada con éxito: ${book.title}",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                // Se muestra un mensaje de error
                Toast.makeText(
                    this@BookDetailActivity,
                    "Error al comprar el libro: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Redirige al usuario a la pantalla de inicio de sesión.
     * Se utiliza cuando el token de sesión no es válido.
     */
    private fun goToLogin() {
        // Redirigir a la pantalla de inicio de sesión
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        const val EXTRA_BOOK_ID = "bookId"
        const val EXTRA_IS_STORE_BOOK = "isStoreBook"
    }
}