package tfg.carlos.wereaderapp.data.remote.datasource

import tfg.carlos.wereaderapp.data.model.auth.LoginRequest
import tfg.carlos.wereaderapp.data.model.auth.LoginResponse
import tfg.carlos.wereaderapp.data.model.auth.RegisterRequest
import tfg.carlos.wereaderapp.data.model.auth.RegisterResponse
import tfg.carlos.wereaderapp.data.model.user.User
import tfg.carlos.wereaderapp.data.remote.Retrofit2Api

class AuthRemoteDataSource {

    suspend fun login(loginRequest: LoginRequest): LoginResponse {
        val response = Retrofit2Api.authApi.login(loginRequest)
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception("Login fallido: ${response.code()}")
        }
    }

    suspend fun register(registerRequest: RegisterRequest): RegisterResponse {
        val response = Retrofit2Api.authApi.register(registerRequest)
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception("Registro fallido: ${response.code()}")
        }
    }

    suspend fun getUserProfile(): User {
        val response = Retrofit2Api.userApi.myProfile()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception("Error al obtener el perfil de usuario: ${response.code()}")
        }
    }
}