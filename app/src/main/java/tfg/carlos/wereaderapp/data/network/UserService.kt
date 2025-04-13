package tfg.carlos.wereaderapp.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import tfg.carlos.wereaderapp.data.model.auth.LoginRequest
import tfg.carlos.wereaderapp.data.model.auth.LoginResponse
import tfg.carlos.wereaderapp.data.model.auth.RegisterRequest
import tfg.carlos.wereaderapp.data.model.auth.RegisterResponse

interface UserService {
    @POST("user/profile")
    suspend fun myProfile(@Body request: LoginRequest): Response<LoginResponse>
}