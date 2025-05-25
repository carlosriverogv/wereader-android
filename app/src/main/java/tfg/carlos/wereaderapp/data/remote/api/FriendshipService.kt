package tfg.carlos.wereaderapp.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import tfg.carlos.wereaderapp.data.model.friendship.UserFriendshipsResponse
import tfg.carlos.wereaderapp.data.model.user.User

interface FriendshipService {
    @GET("friendship/myfriendships")
    suspend fun getAllFriends(): Response<UserFriendshipsResponse>
}