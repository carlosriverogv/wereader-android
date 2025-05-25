package tfg.carlos.wereaderapp.data.repository

import tfg.carlos.wereaderapp.data.remote.datasource.FriendshipRemoteDataSource

class FriendshipRepository(
    val remote: FriendshipRemoteDataSource
) {

    // API METHODS
    /**
     * Obtiene todas las amistades del usuario autenticado.
     * @return Lista de amistades del usuario. UserFriendshipsResponse
     */
    suspend fun getFriendships() = remote.getFriendships()

}