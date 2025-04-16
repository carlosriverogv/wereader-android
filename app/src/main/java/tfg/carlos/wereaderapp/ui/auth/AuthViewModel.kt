package tfg.carlos.wereaderapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.auth0.android.jwt.JWT
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.model.auth.LoginRequest
import tfg.carlos.wereaderapp.data.model.auth.LoginResponse
import tfg.carlos.wereaderapp.data.model.auth.RegisterRequest
import tfg.carlos.wereaderapp.data.repository.AuthRepository

class AuthViewModel(val repository: AuthRepository): ViewModel() {
    private val sessionManager by lazy {
        WeReaderApplication.sessionManager
    }

    suspend fun login(loginRequest: LoginRequest) {
        // Validar los datos de entrada
        if (loginRequest.email.isEmpty() || loginRequest.password.isEmpty()) {
            throw IllegalArgumentException("EL email o contraseña no pueden estar vacíos")
        }
        // Validar el formato del email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(loginRequest.email).matches()) {
            throw IllegalArgumentException("Email no válido")
        }
        // Llamar al repositorio para realizar la llamada a la API guardando el token en el SessionManager
        val loginResponse: LoginResponse = repository.login(loginRequest)

        // Comprobar si la respuesta es válida
        if (!isTokenValid(loginResponse.token.token)) {
            throw IllegalArgumentException("Token no válido")
        }

        // Guardar el token en el SessionManager
        sessionManager.saveToken(loginResponse.token.token)
    }

    suspend fun register(request: RegisterRequest) {
        // Validaciones mínimas
        if (request.name.isEmpty() || request.lastname.isEmpty()) {
            throw IllegalArgumentException("Nombre y apellido son obligatorios")
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(request.email).matches()) {
            throw IllegalArgumentException("Email no válido")
        }

        if (request.password.length < 6) {
            throw IllegalArgumentException("Contraseña demasiado corta")
        }

        // Aquí llamas al repo
        repository.register(request)

        // Hacer login automáticamente después de registrarse
        login(LoginRequest(request.email, request.password))
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
}

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(repository) as T
    }
}