package tfg.carlos.wereaderapp.data.remote.datasource

import org.json.JSONObject
import tfg.carlos.wereaderapp.data.model.friendship.FriendshipRequest
import tfg.carlos.wereaderapp.data.model.user.UserListResponse
import tfg.carlos.wereaderapp.data.remote.Retrofit2Api

class FriendshipRemoteDataSource {

    /**
     * Crea una solicitud de amistad para el usuario autenticado.
     * @param idFriendUser ID del usuario amigo al que se le envía la solicitud.
     */
    suspend fun createFriendship(idFriendUser: String) {
        val response = Retrofit2Api.friendshipApi.createFriendship(
            FriendshipRequest(idFriendUser)
        )
        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody).getString("message")
        } catch (e: Exception) {
                "Error al crear la solicitud de amistad: ${response.code()}"
            }
            throw Exception(errorMessage)
        }
    }

    suspend fun getFriendships(): UserListResponse {
        val response = Retrofit2Api.friendshipApi.getAllFriends()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception("Error al obtener las amistades: ${response.code()}")
        }
    }

    suspend fun getReceivedFriendshipRequests(): UserListResponse {
        val response = Retrofit2Api.friendshipApi.getReceivedFriendshipRequests()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception("Error al obtener las solicitudes de amistad: ${response.code()}")
        }
    }

    suspend fun acceptFriendshipRequest(idFriendUser: String) {
        val response = Retrofit2Api.friendshipApi.acceptFriendshipRequest(
            FriendshipRequest(idFriendUser)
        )
        if (!response.isSuccessful) {
            throw Exception("Error al aceptar la solicitud de amistad: ${response.code()}")
        }
    }

    suspend fun rejectFriendshipRequest(idFriendUser: String) {
        val response = Retrofit2Api.friendshipApi.rejectFriendshipRequest(
            FriendshipRequest(idFriendUser)
        )
        if (!response.isSuccessful) {
            throw Exception("Error al rechazar la solicitud de amistad: ${response.code()}")
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