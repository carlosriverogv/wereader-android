package tfg.carlos.wereaderapp.ui.discover

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import edu.carlosrivero.demo5.utils.isTokenValid
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.databinding.ActivityDiscoverBinding
import tfg.carlos.wereaderapp.ui.library.LibraryActivity
import tfg.carlos.wereaderapp.ui.main.MainActivity
import tfg.carlos.wereaderapp.ui.profile.ProfileActivity

class DiscoverActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiscoverBinding

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

    private fun setupUI() {
        // Se inicializa la vista
        binding = ActivityDiscoverBinding.inflate(layoutInflater)

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
}