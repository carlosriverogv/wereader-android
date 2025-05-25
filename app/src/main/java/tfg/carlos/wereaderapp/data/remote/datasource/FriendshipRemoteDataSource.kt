package tfg.carlos.wereaderapp.data.remote.datasource

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
}