package tfg.carlos.wereaderapp.ui.auth.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import edu.carlosrivero.demo5.utils.checkConnection
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.data.model.auth.RegisterRequest
import tfg.carlos.wereaderapp.data.remote.datasource.AuthRemoteDataSource
import tfg.carlos.wereaderapp.data.repository.AuthRepository
import tfg.carlos.wereaderapp.databinding.ActivityRegisterBinding
import tfg.carlos.wereaderapp.ui.auth.AuthViewModel
import tfg.carlos.wereaderapp.ui.auth.AuthViewModelFactory
import tfg.carlos.wereaderapp.ui.auth.login.LoginActivity
import tfg.carlos.wereaderapp.ui.main.MainActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var viewPager: ViewPager2
    private lateinit var registerData: RegisterRequest

    // Configuramos el ViewModel
    private val vm: AuthViewModel by viewModels {
        val dataSource = AuthRemoteDataSource()
        val repository = AuthRepository(dataSource)
        AuthViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        registerData = RegisterRequest("", 0, "", "",
            "", "", "", "")

        viewPager = findViewById(R.id.registerViewPager)
        viewPager.adapter = RegisterPagerAdapter(this)
        viewPager.isUserInputEnabled = false // sin swipe manual

        // Recoge del sistema si se pulsa el botón de atrás o se realiza el gesto de retroceso
        onBackPressedDispatcher.addCallback(this) {
            val currentItem = viewPager.currentItem
            if (currentItem > 0) {
                goToPreviousStep()
            } else {
                finish()
            }
        }
    }

    fun registerUser() {
        if (checkConnection(this)) {
            lifecycleScope.launch {
                try {
                    vm.register(registerData)
                    goToMain()
                } catch (e: Exception) {
                    Log.e("RegisterActivity", "Excepción en la llamada API", e)
                    Toast.makeText(this@RegisterActivity, getString(R.string.error_register), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.error_no_connection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToMain() {
        // Redirigir a la pantalla de inicio de sesión
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun goToNextStep() {
        viewPager.currentItem += 1
    }

    private fun goToPreviousStep() {
        viewPager.currentItem -= 1
    }

    fun getRegisterData(): RegisterRequest = registerData
}