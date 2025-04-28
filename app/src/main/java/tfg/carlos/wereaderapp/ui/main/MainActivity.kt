package tfg.carlos.wereaderapp.ui.main

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import edu.carlosrivero.demo5.utils.checkConnection
import edu.carlosrivero.demo5.utils.isTokenValid
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.data.local.datasource.LibraryLocalDataSource
import tfg.carlos.wereaderapp.data.remote.Retrofit2Api.libraryApi
import tfg.carlos.wereaderapp.data.remote.datasource.LibraryRemoteDadaSource
import tfg.carlos.wereaderapp.data.repository.LibraryRepository
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

    private val vm: MainViewModel by viewModels {
        val db = (application as WeReaderApplication).weReaderDB
        val localDataSource = LibraryLocalDataSource(db.bookDao())
        val remoteDadaSource = LibraryRemoteDadaSource()
        val repository = LibraryRepository(remoteDadaSource, localDataSource)
        MainViewModelFactory(repository)
    }

    private val adapter = BooksAdapter (
        onClickBookItem = { idBook: String, position: Int ->
            clickedItemPosition = position
            // TODO: Se ejecuta la lectura del libro con FileReader

            // TODO: TEST (Cambiar el estado de lectura del libro y el progreso)
            vm.updateBookReadingStatus(idBook, false) // PARA PRUEBAS
            Toast.makeText(
                this,
                "No leyendo: $idBook",
                Toast.LENGTH_SHORT
            ).show()
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
        } else {
            // TODO: TEST (Logout provisional)
            binding.textViewPage.setOnClickListener() {
                sessionManager.clearToken()
                goToLogin()
            }
        }
    }

    private fun setupUI() {
        // Se inicializa la vista
        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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

        // Se cargan los libros que se están leyendo
        loadReadingBooks()
    }

    private fun loadReadingBooks() {
        binding.readingBooksRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.readingBooksRecyclerView.adapter = adapter

        adapter.submitList(null)

        lifecycleScope.launch {
            vm.books.collect { booksList ->
                Log.d("BooksFragment", "Libros recibidos: ${booksList.size}")
                adapter.submitList(booksList)
            }
        }
    }

    private fun goToLogin() {
        // Redirigir a la pantalla de inicio de sesión
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Todo: TEST (Login provisional)
    private fun fakeLoginToken() {
        val testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Imthcmxvcy5yaXZlcm9nQGdtYWlsLmNvbSIsInN1YiI6IjY3ZTU0NDUzZmFlNDgwODViMzdhMjhjZSIsImlhdCI6MTc0NDcwNDg0NSwiZXhwIjoxNzQ0NzEyMDQ1fQ.DLsM6Y-S6JnC_LJMA8UzAsA3wsLV2TSM-JKmeT9gjn4"

        sessionManager.saveToken(testToken)
    }

    // Todo: TEST (Call API)
    private fun fetchApi() {
        // Usamos Coroutine para hacer la llamada de forma asíncrona
        if (checkConnection(this)) {
            lifecycleScope.launch {
                try {
                    // Hacemos la llamada al API de UserService
                    val response = libraryApi.getAuthUserLibrary()

                    // Si la respuesta es exitosa, la procesamos
                    if (response.isSuccessful) {
                        val library = response.body()

                        // Aquí puedes actualizar la UI con los datos del usuario
                        // Por ejemplo, mostrar el nombre de usuario en un TextView
                        binding.textViewPage.text = library?.id ?: getString(R.string.app_name)

                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("MainActivity", "Error: ${response.code()} - $errorBody")

                        // Si la respuesta no fue exitosa, mostramos un error
                        Toast.makeText(
                            this@MainActivity,
                            "Error: ${response.code()} - $errorBody",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    // Imprime la excepción completa y su tipo
                    Log.e("MainActivity", "Excepción en la llamada API", e)

                    // Mostrar el mensaje de error en un Toast
                    Toast.makeText(this@MainActivity, "Excepción: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Si no hay conexión, mostramos un mensaje
            Toast.makeText(this, getString(R.string.error_no_connection), Toast.LENGTH_SHORT).show()
        }
    }
}