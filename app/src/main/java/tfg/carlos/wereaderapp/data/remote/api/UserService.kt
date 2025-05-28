package tfg.carlos.wereaderapp.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import tfg.carlos.wereaderapp.data.model.user.User
import tfg.carlos.wereaderapp.data.model.user.UserListResponse

interface UserService {
    @GET("user/profile")
    suspend fun myProfile(): Response<User>

    @GET("user/search/{tag}")
    suspend fun searchUserByTag(@Path("tag") tag: String): Response<UserListResponse>
}