package tfg.carlos.wereaderapp.ui.library

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayoutMediator
import edu.carlosrivero.demo5.utils.isTokenValid
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.databinding.ActivityLibraryBinding
import tfg.carlos.wereaderapp.ui.discover.DiscoverActivity
import tfg.carlos.wereaderapp.ui.library.fragments.LibraryPagerAdapter
import tfg.carlos.wereaderapp.ui.main.MainActivity
import tfg.carlos.wereaderapp.ui.profile.ProfileActivity

class LibraryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLibraryBinding

    private val sessionManager by lazy {
        WeReaderApplication.sessionManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val token = sessionManager.getToken()

        if (isTokenValid(token)) {
            setupUI()
        }
    }

    /**
     * Configura la interfaz de usuario de la actividad Library.
     * Se inicializa el binding, se habilita el modo Edge to Edge y se configura el BottomNavigationView
     * y el TabLayout.
     */
    private fun setupUI() {
        // Se inicializa la vista
        binding = ActivityLibraryBinding.inflate(layoutInflater)

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
        // Se carga el TabLayout
        loadTabLayout()
    }

    /**
     * Configura el BottomNavigationView y su comportamiento.
     * Se establece el listener para manejar los clics en los elementos del menú.
     */
    private fun loadBottomNavigation() {
        // Se establece el ID del elemento seleccionado en el BottomNavigationView
        binding.bottomNavigation.selectedItemId = R.id.nav_library

        // Se establece el listener para los elementos del BottomNavigationView
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val intent = when (item.itemId) {
                R.id.nav_home -> Intent(this, MainActivity::class.java)
                R.id.nav_library -> return@setOnItemSelectedListener true
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
     * Carga el TabLayout con los títulos de las pestañas y el adaptador.
     * Se utiliza TabLayoutMediator para vincular el TabLayout con el ViewPager.
     */
    private fun loadTabLayout() {
        val tabTitles = listOf(
            getString(R.string.library_tab_books),
            getString(R.string.library_tab_shared),
            //getString(R.string.library_tab_collections)
        )
        val adapter = LibraryPagerAdapter(this)

        binding.viewPagerLibrary.adapter = adapter

        TabLayoutMediator(binding.tabLayoutLibrary, binding.viewPagerLibrary) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}