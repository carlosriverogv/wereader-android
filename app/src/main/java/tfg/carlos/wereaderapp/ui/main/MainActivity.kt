package tfg.carlos.wereaderapp.ui.main

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.databinding.ActivityMainBinding
import tfg.carlos.wereaderapp.ui.auth.login.LoginActivity
import edu.carlosrivero.demo5.utils.isTokenValid
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.data.entity.BookEntity
import tfg.carlos.wereaderapp.data.local.datasource.LibraryLocalDataSource
import tfg.carlos.wereaderapp.data.remote.datasource.LibraryRemoteDadaSource
import tfg.carlos.wereaderapp.data.repository.LibraryRepository
import tfg.carlos.wereaderapp.ui.bookDetail.BookDetailActivity
import tfg.carlos.wereaderapp.ui.reader.ReaderActivity
import tfg.carlos.wereaderapp.utils.BookMenuHandler
import tfg.carlos.wereaderapp.ui.discover.DiscoverActivity
import tfg.carlos.wereaderapp.ui.library.LibraryActivity
import tfg.carlos.wereaderapp.ui.library.fragments.library.BooksAdapter
import tfg.carlos.wereaderapp.ui.profile.ProfileActivity


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var clickedItemPosition: Int = RecyclerView.NO_POSITION

    private val sessionManager by lazy {
        WeReaderApplication.sessionManager
    }

    // ViewModel para la actividad principal
    private val vm: MainViewModel by viewModels {
        val db = (application as WeReaderApplication).weReaderDB
        val localDataSource = LibraryLocalDataSource(db.bookDao())
        val remoteDadaSource = LibraryRemoteDadaSource()
        val repository = LibraryRepository(remoteDadaSource, localDataSource)
        MainViewModelFactory(repository)
    }

    /**
     * Adaptador para los libros que se están leyendo.
     * Permite manejar los eventos de clic y clic largo en los elementos de la lista.
     */
    private val readingBooksAdapter = BooksAdapter(
        onClickBookItem = { book: BookEntity, position: Int ->
            clickedItemPosition = position
            // Se ejecuta la lectura del libro con Readium
            lifecycleScope.launch {
                openReaderActivity(book.id)
            }
        },
        onLongClickBookItem = { idBook: String, position: Int, isPending: Boolean ->
            clickedItemPosition = position
            showBookOptionsMenu(binding.readingBooksRecyclerView, idBook, isPending, position)
        }
    )

    /**
     * Adaptador para los libros pendientes.
     * Permite manejar los eventos de clic y clic largo en los elementos de la lista.
     */
    private val pendingBooksAdapter = BooksAdapter(
        onClickBookItem = { book: BookEntity, position: Int ->
            clickedItemPosition = position
            vm.updateBookReadingStatus(book.id, true) // Para pruebas
            // Se ejecuta la lectura del libro con Readium
            lifecycleScope.launch {
                openReaderActivity(book.id)
            }
        },
        onLongClickBookItem = { idBook: String, position: Int, isPending: Boolean ->
            clickedItemPosition = position
            showBookOptionsMenu(binding.pendingBooksRecyclerView, idBook, isPending, position)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Se obtiene el token de sesión y se verifica su validez
        val token = sessionManager.getToken()

        if (isTokenValid(token)) {
            setupUI()
        }
    }

    override fun onResume() {
        super.onResume()
        val token = sessionManager.getToken()

        if (!isTokenValid(token)) {
            goToLogin()
        }
    }

    /**
     * Configura la interfaz de usuario de la actividad Main.
     * Se inicializa el binding, se habilita el modo Edge to Edge y se configura el BottomNavigationView
     * y los RecyclerViews para los libros que se están leyendo y los libros pendientes.
     */
    private fun setupUI() {
        // Se inicializa la vista
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Se habilita el modo Edge to Edge
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Se carga el BottomNavigationView
        loadBottomNavigation()
        // Se cargan los libros que se están leyendo
        loadReadingBooks()
        // Se cargan los libros pendientes
        loadPendingBooks()
    }

    private fun loadBottomNavigation() {
        // Se establece el ID del elemento seleccionado en el BottomNavigationView
        binding.bottomNavigation.selectedItemId = R.id.nav_home

        // Se establece el listener para los elementos del BottomNavigationView
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val intent = when (item.itemId) {
                R.id.nav_home -> return@setOnItemSelectedListener true
                R.id.nav_library -> Intent(this, LibraryActivity::class.java)
                R.id.nav_discover -> Intent(this, DiscoverActivity::class.java)
                R.id.nav_profile -> Intent(this, ProfileActivity::class.java)
                else -> return@setOnItemSelectedListener false
            }
            val options = ActivityOptions.makeCustomAnimation(this, 0, 0)
            startActivity(intent, options.toBundle())
            finish()
            true
        }
    }

    /**
     * Carga los libros que se están leyendo en el RecyclerView correspondiente.
     * Se configura el LayoutManager y el Adapter, y se observa la lista de libros
     * para actualizar la interfaz cuando cambie.
     */
    private fun loadReadingBooks() {
        binding.readingBooksRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.readingBooksRecyclerView.adapter = readingBooksAdapter

        readingBooksAdapter.submitList(null)

        lifecycleScope.launch {
            vm.readingBooks.collect { booksList ->
                readingBooksAdapter.submitList(booksList)
            }
        }
    }

    /**
     * Carga los libros pendientes en el RecyclerView correspondiente.
     * Se configura el LayoutManager y el Adapter, y se observa la lista de libros
     * para actualizar la interfaz cuando cambie.
     */
    private fun loadPendingBooks() {
        binding.pendingBooksRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.pendingBooksRecyclerView.adapter = pendingBooksAdapter

        pendingBooksAdapter.submitList(null)

        lifecycleScope.launch {
            vm.pendingBooks.collect { booksList ->
                pendingBooksAdapter.submitList(booksList)
            }
        }
    }

    /**
     * Abre la actividad de lectura con el ID del libro proporcionado.
     * Se obtiene la URL del libro y se inicia la actividad ReaderActivity.
     */
    private suspend fun openReaderActivity(idBook: String) {
        vm.getBookById(idBook).let { book ->
            val epubPath = book.epubUrl
            val intent = Intent(this@MainActivity, ReaderActivity::class.java)
            intent.putExtra("bookPath", epubPath)
            intent.putExtra("bookId", book.id)
            startActivity(intent)
        }
    }

    /**
     * Abre la actividad de detalles del libro con el ID del libro proporcionado.
     * Se pasa el ID del libro y se indica que no es un libro de la tienda.
     */
    private fun openBookDetailActivity(idBook: String) {
        val intent = Intent(this, BookDetailActivity::class.java).apply {
            putExtra(BookDetailActivity.EXTRA_BOOK_ID, idBook)
            putExtra(BookDetailActivity.EXTRA_IS_STORE_BOOK, false)
        }
        startActivity(intent)
    }

    /**
     * Muestra el menú de opciones del libro en el RecyclerView correspondiente.
     * Permite al usuario realizar acciones como leer, ver detalles, actualizar estado de lectura,
     * y marcar como leído o no leído.
     */
    private fun showBookOptionsMenu(
        recyclerView: RecyclerView,
        idBook: String,
        isPending: Boolean,
        position: Int
    ) {
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position) ?: return
        val anchorView = viewHolder.itemView

        BookMenuHandler.show(
            context = this,
            anchorView = anchorView,
            isPending = isPending,
            onRead = {
                lifecycleScope.launch {
                    openReaderActivity(idBook)
                }
            },
            onDetail = {
                openBookDetailActivity(idBook)
            },
            updatePending = { pending ->
                vm.updateBookPendingStatus(idBook, pending)
            },
            updateReading = { reading ->
                vm.updateBookReadingStatus(idBook, reading)
            },
            updateMarkReadOrUnreadBook = { isRead ->
                vm.updateMarkReadOrUnreadBook(idBook, isRead)
            },
        )
    }

    // Función para redirigir a la pantalla de inicio de sesión
    private fun goToLogin() {
        // Redirigir a la pantalla de inicio de sesión
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}