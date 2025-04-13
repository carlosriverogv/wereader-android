package tfg.carlos.wereaderapp.data.network

import retrofit2.Response
import retrofit2.http.POST
import tfg.carlos.wereaderapp.data.model.user.User

interface UserService {
    @POST("user/profile")
    suspend fun myProfile(): Response<User>
}