package tfg.carlos.wereaderapp.data.repository

import tfg.carlos.wereaderapp.data.model.auth.LoginRequest
import tfg.carlos.wereaderapp.data.model.auth.RegisterRequest
import tfg.carlos.wereaderapp.data.remote.datasource.AuthRemoteDataSource

class AuthRepository(val dataSource: AuthRemoteDataSource) {

    // API Methods
    suspend fun login(loginRequest: LoginRequest) = dataSource.login(loginRequest)

    suspend fun register(registerRequest: RegisterRequest) = dataSource.register(registerRequest)

    // ROOM Methods
}