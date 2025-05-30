package tfg.carlos.wereaderapp.ui.discover

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commitNow
import androidx.recyclerview.widget.LinearLayoutManager
import edu.carlosrivero.demo5.utils.isTokenValid
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.remote.datasource.BookRemoteDataSource
import tfg.carlos.wereaderapp.data.repository.BookRepository
import tfg.carlos.wereaderapp.databinding.ActivityDiscoverBinding
import tfg.carlos.wereaderapp.ui.auth.login.LoginActivity
import tfg.carlos.wereaderapp.ui.bookDetail.BookDetailActivity
import tfg.carlos.wereaderapp.ui.discover.search.BookSearchFragment
import tfg.carlos.wereaderapp.ui.library.LibraryActivity
import tfg.carlos.wereaderapp.ui.main.MainActivity
import tfg.carlos.wereaderapp.ui.profile.ProfileActivity

class DiscoverActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiscoverBinding

    private val sessionManager by lazy {
        WeReaderApplication.sessionManager
    }

    private val discoverViewModel: DiscoverViewModel by viewModels {
        val bookRemoteDadaSource = BookRemoteDataSource()
        val bookRepository = BookRepository(bookRemoteDadaSource)
        DiscoverViewModelFactory(bookRepository)
    }


    private val bestsellersAdapter = BooksDiscoverAdapter(
        onClickBookItem = { book, position ->
            openBookDetailActivity(book.id)
        }
    )

    private val newReleasesAdapter = BooksDiscoverAdapter(
        onClickBookItem = { book, position ->
            openBookDetailActivity(book.id)
        }
    )

    private val recommendedBooksAdapter = BooksDiscoverAdapter(
        onClickBookItem = { book, position ->
            openBookDetailActivity(book.id)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val token = sessionManager.getToken()

        if (isTokenValid(token)) {
            setupUI()
            val activeFragment = supportFragmentManager.findFragmentById(R.id.fragment_search_container)
            if (activeFragment is BookSearchFragment) {
                binding.fragmentSearchContainer.visibility = View.VISIBLE
                binding.scrollView.visibility = View.GONE
                binding.toolbarDiscover.visibility = View.GONE
                binding.bottomNavigation.visibility = View.GONE
            }
            handleBackPressed()
        }
    }

    private fun setupUI() {
        // Se inicializa la vista
        binding = ActivityDiscoverBinding.inflate(layoutInflater)

        // Se habilita el modo Edge to Edge
        enableEdgeToEdge()
        setSupportActionBar(binding.toolbarDiscover)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Se carga el BottomNavigationView
        loadBottomNavigation()

        // Se cargan los libros más vendidos
        loadBestsellers()
        // Se cargan los nuevos lanzamientos
        loadNewReleases()
        // Se cargan los libros recomendados
        loadRecommendedBooks()
    }

    /*
     * Configura el menú de opciones de la actividad.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.discover_toolbar_menu, menu)
        return true
    }

    /**
     * Maneja la selección de elementos del menú de opciones.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            R.id.opt_search -> {
                // Mostrar el contenedor del fragmento
                binding.fragmentSearchContainer.visibility = View.VISIBLE
                // Ocultar el scrollView principal
                binding.scrollView.visibility = View.GONE
                binding.toolbarDiscover.visibility = View.GONE
                binding.bottomNavigation.visibility = View.GONE

                // Se inicia el fragmento de búsqueda de libros
                supportFragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_MATCH_ACTIVITY_OPEN)
                    .replace(R.id.fragment_search_container, BookSearchFragment())
                    .addToBackStack(null)
                    .commit()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Maneja el evento de retroceso del sistema.
     * Si hay un fragmento de búsqueda activo, lo cierra y muestra el contenido principal.
     * Si no hay fragmento activo, permite que el sistema maneje el retroceso normalmente.
     */
    private fun handleBackPressed() {
        onBackPressedDispatcher.addCallback(this) {
            val fragment = supportFragmentManager.findFragmentById(R.id.fragment_search_container)
            if (fragment is BookSearchFragment) {
                // Cierra el fragmento y muestra el contenido principal
                supportFragmentManager.beginTransaction().remove(fragment).commit()
                binding.fragmentSearchContainer.visibility = View.GONE
                binding.scrollView.visibility = View.VISIBLE
                binding.toolbarDiscover.visibility = View.VISIBLE
                binding.bottomNavigation.visibility = View.VISIBLE
            } else {
                // Si no hay fragmento activo, permite que el sistema maneje el back normalmente
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val token = sessionManager.getToken()

        if (!isTokenValid(token)) {
            goToLogin()
        }
    }

    private fun loadBottomNavigation() {
        // Se establece el ID del elemento seleccionado en el BottomNavigationView
        binding.bottomNavigation.selectedItemId = R.id.nav_discover

        // Se establece el listener para los elementos del BottomNavigationView
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val intent = when (item.itemId) {
                R.id.nav_home -> Intent(this, MainActivity::class.java)
                R.id.nav_library -> Intent(this, LibraryActivity::class.java)
                R.id.nav_discover -> return@setOnItemSelectedListener true
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
     * Cargar los libros más vendidos
     *
     */
    private fun loadBestsellers() {
        binding.bestSellersRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.bestSellersRecyclerView.adapter = bestsellersAdapter

        bestsellersAdapter.submitList(null)

        discoverViewModel.bestsellersBooks.observe(this) { bookList ->
            bestsellersAdapter.submitList(bookList)
        }
    }

    /**
     * Cargar los nuevos lanzamientos
     */
    private fun loadNewReleases() {
        binding.newReleasesRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.newReleasesRecyclerView.adapter = newReleasesAdapter

        newReleasesAdapter.submitList(null)

        discoverViewModel.newReleasesBooks.observe(this) { bookList ->
            newReleasesAdapter.submitList(bookList)
        }
    }

    /**
     * Cargar los libros recomendados
     */
    private fun loadRecommendedBooks() {
        binding.recommendedRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recommendedRecyclerView.adapter = recommendedBooksAdapter

        recommendedBooksAdapter.submitList(null)

        // PROVISIONAL: Aquí se debería obtener la lista de libros recomendados
        discoverViewModel.recommendedBooks.observe(this) { bookList ->
            recommendedBooksAdapter.submitList(bookList)
        }
    }

    /**
     * Abre la actividad de detalle del libro.
     * @param idBook El ID del libro a mostrar.
     */
    private fun openBookDetailActivity(idBook: String) {
        val intent = Intent(this, BookDetailActivity::class.java).apply {
            putExtra(BookDetailActivity.EXTRA_BOOK_ID, idBook)
            putExtra(BookDetailActivity.EXTRA_IS_STORE_BOOK, true)
        }
        startActivity(intent)
    }

    private fun goToLogin() {
        // Redirigir a la pantalla de inicio de sesión
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}