package tfg.carlos.wereaderapp.data.remote.datasource

import tfg.carlos.wereaderapp.data.model.friendship.FriendshipRequest
import tfg.carlos.wereaderapp.data.model.friendship.UserFriendshipsResponse
import tfg.carlos.wereaderapp.data.remote.Retrofit2Api

class FriendshipRemoteDataSource {

    suspend fun getFriendships(): UserFriendshipsResponse {
        val response = Retrofit2Api.friendshipApi.getAllFriends()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception("Error al obtener las amistades: ${response.code()}")
        }
    }

    suspend fun getReceivedFriendshipRequests(): UserFriendshipsResponse {
        val response = Retrofit2Api.friendshipApi.getReceivedFriendshipRequests()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception("Error al obtener las solicitudes de amistad: ${response.code()}")
        }
    }

    suspend fun deleteMyFriendship(idFriendUser: String) {
        val response = Retrofit2Api.friendshipApi.deleteMyFriendship(
            FriendshipRequest(idFriendUser)
        )
        if (!response.isSuccessful) {
            throw Exception("Error al eliminar la amistad: ${response.code()}")
        }
    }
}