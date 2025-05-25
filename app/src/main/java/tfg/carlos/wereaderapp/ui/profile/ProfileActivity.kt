package tfg.carlos.wereaderapp.ui.profile

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.tabs.TabLayoutMediator
import edu.carlosrivero.demo5.utils.checkConnection
import edu.carlosrivero.demo5.utils.isTokenValid
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.local.datasource.LibraryLocalDataSource
import tfg.carlos.wereaderapp.data.remote.datasource.AuthRemoteDataSource
import tfg.carlos.wereaderapp.data.remote.datasource.LibraryRemoteDadaSource
import tfg.carlos.wereaderapp.data.repository.AuthRepository
import tfg.carlos.wereaderapp.data.repository.LibraryRepository
import tfg.carlos.wereaderapp.databinding.ActivityProfileBinding
import tfg.carlos.wereaderapp.ui.auth.login.LoginActivity
import tfg.carlos.wereaderapp.ui.avatar.AvatarProvider.getAvatarById
import tfg.carlos.wereaderapp.ui.discover.DiscoverActivity
import tfg.carlos.wereaderapp.ui.library.LibraryActivity
import tfg.carlos.wereaderapp.ui.library.fragments.LibraryPagerAdapter
import tfg.carlos.wereaderapp.ui.main.MainActivity
import tfg.carlos.wereaderapp.ui.profile.fragments.ProfilePagerAdapter

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    private val sessionManager by lazy {
        WeReaderApplication.sessionManager
    }

    private val authViewModel: ProfileViewModel by viewModels {
        val db = (this.application as WeReaderApplication).weReaderDB
        val libraryLocalDataSource = LibraryLocalDataSource(db.bookDao())
        val libraryRemoteDadaSource = LibraryRemoteDadaSource()
        val libraryRepository = LibraryRepository(libraryRemoteDadaSource, libraryLocalDataSource)

        val authRemoteDadaSource = AuthRemoteDataSource()
        val authRepository = AuthRepository(authRemoteDadaSource)
        ProfileViewModelFactory(authRepository, libraryRepository)
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
        } else {
            binding.ivLogout.setOnClickListener {
                showLogoutConfirmationDialog()
            }
        }
    }

    /** TODO: Añadir los textos a los strings.xml
     * Muestra un diálogo de confirmación para cerrar sesión.
     * Si el usuario confirma, se limpia el token de sesión y se redirige a la pantalla de inicio de sesión.
     */
    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                sessionManager.clearToken()
                goToLogin()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Configura la interfaz de usuario de la actividad Profile.
     * Se inicializa el binding, se habilita el modo Edge to Edge y se configura el BottomNavigationView
     * y el TabLayout.
     */
    private fun setupIU() {
        // Se inicializa la vista
        binding = ActivityProfileBinding.inflate(layoutInflater)

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
        // Se muestra el perfil del usuario
        showUserProfile()
    }

    /**
     * Configura el BottomNavigationView y su comportamiento.
     * Se establece el listener para manejar los clics en los elementos del menú.
     */
    private fun loadBottomNavigation() {
        // Se establece el ID del elemento seleccionado en el BottomNavigationView
        binding.bottomNavigation.selectedItemId = R.id.nav_profile

        // Se establece el listener para los elementos del BottomNavigationView
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val intent = when (item.itemId) {
                R.id.nav_home -> Intent(this, MainActivity::class.java)
                R.id.nav_library -> Intent(this, LibraryActivity::class.java)
                R.id.nav_discover -> Intent(this, DiscoverActivity::class.java)
                R.id.nav_profile -> return@setOnItemSelectedListener true
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
            getString(R.string.profile_tab_friend),
            getString(R.string.profile_tab_request),
            getString(R.string.profile_tab_add_friend)
        )
        val adapter = ProfilePagerAdapter(this)

        binding.viewPagerProfile.adapter = adapter

        TabLayoutMediator(binding.tabLayoutProfile, binding.viewPagerProfile) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    /**
     * Muestra el perfil del usuario actual.
     * Observa los datos del usuario y actualiza la interfaz de usuario con la información del perfil.
     */
    private fun showUserProfile() {
        if (checkConnection(this)) {
            authViewModel.user.observe(this) { user ->
                user?.let {
                    binding.userNameText.text = getString(R.string.profile_full_name,
                        it.name, it.lastname)
                    binding.userTagText.text = it.tag
                    setAvatarImage(binding.profileAvatarImage, it.avatar)

                    binding.authorFav.text = it.authorFav
                    binding.genreFav.text = it.genreFav

                    authViewModel.getPendingBooksCount().observe(this) { count ->
                        binding.pendingBooksCount.text = count.toString()
                    }

                    authViewModel.getTotalBooksCount().observe(this) { count ->
                        binding.totalBooksCount.text = count.toString()
                    }

                    authViewModel.getFinishedBooksCount().observe(this) { count ->
                        binding.finishedBooksCount.text = count.toString()
                    }
                }
            }
        } else {
            Toast.makeText(
                this,
                getString(R.string.error_no_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Establece la imagen del avatar en el ImageView proporcionado.
     * Utiliza Glide para cargar la imagen y aplicarle un recorte circular.
     *
     * @param imageView El ImageView donde se mostrará el avatar.
     * @param avatarId El ID del avatar a mostrar.
     */
    private fun setAvatarImage(imageView: ImageView, avatarId: Int) {
        val avatar = getAvatarById(avatarId)
        avatar?.let {
            Glide.with(imageView.context)
                .load(it.drawableRes)
                .transform(CircleCrop())
                .into(imageView)
        }
    }

    /*private fun showAvatarSelectionDialog(onAvatarSelected: (Int) -> Unit) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_avatar_selection, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.avatarRecyclerView)

        recyclerView.adapter = AvatarAdapter(AvatarProvider.getAllAvatars()) { avatarId ->
            onAvatarSelected(avatarId)
            alertDialog.dismiss()
        }

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        alertDialog.show()
    }*/

    private fun goToLogin() {
        // Redirigir a la pantalla de inicio de sesión
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


}