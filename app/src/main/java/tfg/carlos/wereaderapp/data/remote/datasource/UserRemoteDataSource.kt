package tfg.carlos.wereaderapp.data.remote.datasource

import tfg.carlos.wereaderapp.data.model.user.User
import tfg.carlos.wereaderapp.data.model.user.UserListResponse
import tfg.carlos.wereaderapp.data.remote.Retrofit2Api

class UserRemoteDataSource {
    suspend fun getUserProfile(): User {
        val response = Retrofit2Api.userApi.myProfile()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception("Error al obtener el perfil de usuario: ${response.code()}")
        }
    }

    suspend fun searchUserByTag(tag: String): UserListResponse {
        val response = Retrofit2Api.userApi.searchUserByTag(tag)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                return body
            } else {
                // Si el cuerpo es inesperadamente null
                throw Exception("Respuesta vacía del servidor al buscar usuario por etiqueta.")
            }
        } else {
            throw Exception("Error al buscar usuario por etiqueta: ${response.code()}")
        }
    }
}