package tfg.carlos.wereaderapp.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import tfg.carlos.wereaderapp.data.model.user.User

interface UserService {
    @GET("user/profile")
    suspend fun myProfile(): Response<User>
}