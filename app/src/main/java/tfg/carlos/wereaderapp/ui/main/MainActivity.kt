package tfg.carlos.wereaderapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.databinding.ActivityMainBinding
import tfg.carlos.wereaderapp.ui.auth.login.LoginActivity
import com.auth0.android.jwt.JWT
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.data.remote.Retrofit2Api.userApi

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val sessionManager by lazy {
        WeReaderApplication.sessionManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //fakeLoginToken()

        // Verificar si hay token
        val token = sessionManager.getToken()

        if (!isTokenValid(token)) {
            // Si no hay token, redirigir a la pantalla de inicio de sesión
            goToLogin()
        } else {
            // Si hay token, continuar con la lógica de la aplicación
            // Aquí puedes cargar los datos del usuario o cualquier otra cosa que necesites hacer
            // ...
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            fetchUserProfile()

            enableEdgeToEdge()
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

    }

    private fun goToLogin() {
        // Redirigir a la pantalla de inicio de sesión
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

        // Cerrar la actividad actual
        finish()
    }


    private fun isTokenValid(token: String?): Boolean {
        if (token.isNullOrEmpty()) return false

        return try {
            val jwt = JWT(token)

            // Comprueba si ha expirado
            !jwt.isExpired(10) // 10 = margen de tolerancia en segundos

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun fakeLoginToken() {
        val testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Imthcmxvcy5yaXZlcm9nQGdtYWlsLmNvbSIsInN1YiI6IjY3ZTU0NDUzZmFlNDgwODViMzdhMjhjZSIsImlhdCI6MTc0NDcwNDg0NSwiZXhwIjoxNzQ0NzEyMDQ1fQ.DLsM6Y-S6JnC_LJMA8UzAsA3wsLV2TSM-JKmeT9gjn4"

        sessionManager.saveToken(testToken)
    }

    private fun fetchUserProfile() {
        // Usamos Coroutine para hacer la llamada de forma asíncrona
        lifecycleScope.launch {
            try {
                // Hacemos la llamada al API de UserService
                val response = userApi.myProfile()

                // Si la respuesta es exitosa, la procesamos
                if (response.isSuccessful) {
                    val user = response.body()

                    // Aquí puedes actualizar la UI con los datos del usuario
                    // Por ejemplo, mostrar el nombre de usuario en un TextView
                    binding.textViewName.text = user?.tag ?: "Nombre no disponible"

                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("MainActivity", "Error: ${response.code()} - $errorBody")

                    // Si la respuesta no fue exitosa, mostramos un error
                    Toast.makeText(this@MainActivity, "Error: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()

                }
            } catch (e: Exception) {
                // Imprime la excepción completa y su tipo
                Log.e("MainActivity", "Excepción en la llamada API", e)

                // Mostrar el mensaje de error en un Toast
                Toast.makeText(this@MainActivity, "Excepción: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}