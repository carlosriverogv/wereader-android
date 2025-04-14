package tfg.carlos.wereaderapp.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import tfg.carlos.wereaderapp.data.model.auth.*

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}