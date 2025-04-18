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
import edu.carlosrivero.demo5.utils.checkConnection
import edu.carlosrivero.demo5.utils.isTokenValid
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

        val token = sessionManager.getToken()

        if (isTokenValid(token)) {
            binding = ActivityMainBinding.inflate(layoutInflater)

            enableEdgeToEdge()
            setContentView(binding.root)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            // Todo: TEST
            fetchUserProfile()
        }
    }

    override fun onResume() {
        super.onResume()
        val token = sessionManager.getToken()

        if (!isTokenValid(token)) {
            goToLogin()
        }
    }
    private fun goToLogin() {
        // Redirigir a la pantalla de inicio de sesión
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Todo: TEST
    private fun fakeLoginToken() {
        val testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Imthcmxvcy5yaXZlcm9nQGdtYWlsLmNvbSIsInN1YiI6IjY3ZTU0NDUzZmFlNDgwODViMzdhMjhjZSIsImlhdCI6MTc0NDcwNDg0NSwiZXhwIjoxNzQ0NzEyMDQ1fQ.DLsM6Y-S6JnC_LJMA8UzAsA3wsLV2TSM-JKmeT9gjn4"

        sessionManager.saveToken(testToken)
    }

    // Todo: TEST
    private fun fetchUserProfile() {
        // Usamos Coroutine para hacer la llamada de forma asíncrona
        if (checkConnection(this)) {
            lifecycleScope.launch {
                try {
                    // Hacemos la llamada al API de UserService
                    val response = userApi.myProfile()

                    // Si la respuesta es exitosa, la procesamos
                    if (response.isSuccessful) {
                        val user = response.body()

                        // Aquí puedes actualizar la UI con los datos del usuario
                        // Por ejemplo, mostrar el nombre de usuario en un TextView
                        binding.textViewName.text = user?.tag ?: getString(R.string.app_name)

                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("MainActivity", "Error: ${response.code()} - $errorBody")

                        // Si la respuesta no fue exitosa, mostramos un error
                        Toast.makeText(
                            this@MainActivity,
                            "Error: ${response.code()} - $errorBody",
                            Toast.LENGTH_LONG
                        ).show()

                        // Borrar el token
                        sessionManager.clearToken()

                        // Redirigir a la pantalla de inicio de sesión
                        goToLogin()
                    }
                } catch (e: Exception) {
                    // Imprime la excepción completa y su tipo
                    Log.e("MainActivity", "Excepción en la llamada API", e)

                    // Mostrar el mensaje de error en un Toast
                    Toast.makeText(this@MainActivity, "Excepción: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            // Si no hay conexión, mostramos un mensaje
            Toast.makeText(this, getString(R.string.error_no_connection), Toast.LENGTH_SHORT).show()
        }
    }
}