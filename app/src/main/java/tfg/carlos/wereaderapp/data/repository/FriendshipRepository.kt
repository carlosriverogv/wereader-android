package tfg.carlos.wereaderapp.data.repository

import tfg.carlos.wereaderapp.data.remote.datasource.FriendshipRemoteDataSource
import tfg.carlos.wereaderapp.data.remote.datasource.LibraryRemoteDadaSource

class FriendshipRepository(
    private val friendshipRemoteDataSource: FriendshipRemoteDataSource,
) {

    // API METHODS
    /**
     * Obtiene todas las amistades del usuario autenticado.
     * @return Lista de amistades del usuario. UserFriendshipsResponse
     */
    suspend fun getFriendships() = friendshipRemoteDataSource.getFriendships()

    /**
     * Obtiene las solicitudes de amistad recibidas por el usuario autenticado.
     */
    suspend fun getReceivedFriendshipRequests() =
        friendshipRemoteDataSource.getReceivedFriendshipRequests()

    /**
     * Acepta una solicitud de amistad del usuario autenticado.
     * @param idFriendUser ID del usuario amigo a aceptar.
     */
    suspend fun acceptFriendshipRequest(idFriendUser: String) =
        friendshipRemoteDataSource.acceptFriendshipRequest(idFriendUser)

    /**
     * Rechaza una solicitud de amistad del usuario autenticado.
     * @param idFriendUser ID del usuario amigo a rechazar.
     */
    suspend fun rejectFriendshipRequest(idFriendUser: String) =
        friendshipRemoteDataSource.rejectFriendshipRequest(idFriendUser)

    /**
     * Elimina una amistad del usuario autenticado.
     * @param idFriendUser ID del usuario amigo a eliminar.
     */
    suspend fun deleteMyFriendship(idFriendUser: String) =
        friendshipRemoteDataSource.deleteMyFriendship(idFriendUser)

}