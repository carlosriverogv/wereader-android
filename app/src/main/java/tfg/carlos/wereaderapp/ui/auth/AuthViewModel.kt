package tfg.carlos.wereaderapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tfg.carlos.wereaderapp.data.model.auth.LoginRequest
import tfg.carlos.wereaderapp.data.repository.AuthRepository

class AuthViewModel(val repository: AuthRepository): ViewModel() {

    suspend fun login(email: String, password: String) {
        // Validar los datos de entrada
        if (email.isEmpty() || password.isEmpty()) {
            throw IllegalArgumentException("Email y contraseña no pueden estar vacíos")
        }
        // Validar el formato del email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            throw IllegalArgumentException("Email no válido")
        }
        // Validar la longitud de la contraseña
        if (password.length < 8) {
            throw IllegalArgumentException("La contraseña debe tener al menos 8 caracteres")
        }
        // Validar la complejidad de la contraseña
        if (!password.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$"))) {
            throw IllegalArgumentException("La contraseña debe contener al menos una minúscula, una mayúscula, un número y un símbolo")
        }
        // Llamar al repositorio para realizar la llamada a la API
        repository.login(LoginRequest(email, password))
    }
}

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(repository) as T
    }
}