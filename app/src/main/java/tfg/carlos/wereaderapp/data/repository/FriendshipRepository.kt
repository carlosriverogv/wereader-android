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
}