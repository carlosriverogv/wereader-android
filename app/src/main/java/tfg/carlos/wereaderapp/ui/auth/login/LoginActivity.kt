package tfg.carlos.wereaderapp.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.data.model.auth.LoginRequest
import tfg.carlos.wereaderapp.data.remote.datasource.AuthRemoteDataSource
import tfg.carlos.wereaderapp.data.repository.AuthRepository
import tfg.carlos.wereaderapp.databinding.ActivityLoginBinding
import tfg.carlos.wereaderapp.ui.auth.AuthViewModel
import tfg.carlos.wereaderapp.ui.auth.AuthViewModelFactory
import tfg.carlos.wereaderapp.ui.auth.register.RegisterActivity
import tfg.carlos.wereaderapp.ui.main.MainActivity

class LoginActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    // Configuramos el ViewModel
    private val vm: AuthViewModel by viewModels {
        val dataSource = AuthRemoteDataSource()
        val repository = AuthRepository(dataSource)
        AuthViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        login()
    }

    private fun login() {
        // Al pulsar el botón de inicio de sesión
        binding.loginButton.setOnClickListener {
            // Obtener los datos de entrada
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            val loginRequest = LoginRequest(email, password)

            lifecycleScope.launch {
                try {
                    // Llamar al ViewModel para iniciar sesión
                    vm.login(loginRequest)

                    // Si el inicio de sesión es exitoso, redirigir a la pantalla principal
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    // Mostrar un mensaje de error
                    e.printStackTrace()

                    // Mostrar un mensaje de error en UI
                    Toast.makeText(
                        this@LoginActivity,
                        "${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        }
        // Al pulsar el botón de registro
        binding.registerLink.setOnClickListener {
            // Redirigir a la pantalla de registro
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}