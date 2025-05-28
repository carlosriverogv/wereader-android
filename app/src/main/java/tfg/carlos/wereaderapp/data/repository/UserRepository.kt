package tfg.carlos.wereaderapp.data.repository

import tfg.carlos.wereaderapp.data.remote.datasource.UserRemoteDataSource

class UserRepository(private val dataSource: UserRemoteDataSource) {

    // API METHODS
    suspend fun getUserProfile() = dataSource.getUserProfile()

    suspend fun searchUserByTag(tag: String) = dataSource.searchUserByTag(tag)
}