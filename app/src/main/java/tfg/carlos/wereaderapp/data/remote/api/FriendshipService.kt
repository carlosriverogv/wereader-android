package tfg.carlos.wereaderapp.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import tfg.carlos.wereaderapp.data.model.friendship.FriendshipResponse
import tfg.carlos.wereaderapp.data.model.friendship.FriendshipRequest
import tfg.carlos.wereaderapp.data.model.user.UserListResponse

interface FriendshipService {
    @POST("friendship")
    suspend fun createFriendship(@Body request: FriendshipRequest)
        : Response<FriendshipResponse>

    @GET("friendship/myFriends")
    suspend fun getAllFriends(): Response<UserListResponse>

    @GET("friendship/receivedRequestFriendships")
    suspend fun getReceivedFriendshipRequests(): Response<UserListResponse>

    @POST("friendship/deleteMyFriendship")
    suspend fun deleteMyFriendship(@Body request: FriendshipRequest)
        : Response<FriendshipResponse>

    @PATCH("friendship/accept")
    suspend fun acceptFriendshipRequest(@Body request: FriendshipRequest)
        : Response<FriendshipResponse>

    @PATCH("friendship/reject")
    suspend fun rejectFriendshipRequest(@Body request: FriendshipRequest)
        : Response<FriendshipResponse>
}