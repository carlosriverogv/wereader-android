package tfg.carlos.wereaderapp.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.data.remote.datasource.AuthRemoteDataSource
import tfg.carlos.wereaderapp.data.repository.AuthRepository
import tfg.carlos.wereaderapp.databinding.ActivityLoginBinding
import tfg.carlos.wereaderapp.ui.main.MainActivity

class LoginActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel

    // Configuramos el ViewModel
    private val vm: AuthViewModel by viewModels {
        val dataSource = AuthRemoteDataSource()
        val repository = AuthRepository(dataSource)
        AuthViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.loginButton.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    vm.login(email, password)
                    // Se maneja la respuesta de la API

                    // Por ejemplo, redirigir a la pantalla principal de la aplicación
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    // Manejar errores de inicio de sesión
                    e.printStackTrace()
                }
            }
        }
    }
}