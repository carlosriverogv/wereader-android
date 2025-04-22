package tfg.carlos.wereaderapp.data.remote.datasource

import tfg.carlos.wereaderapp.data.model.library.LibraryResponse
import tfg.carlos.wereaderapp.data.remote.Retrofit2Api

class LibraryRemoteDadaSource {
    suspend fun getAuthUserLibrary(): LibraryResponse {
        val response = Retrofit2Api.libraryApi.getAuthUserLibrary()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception("Error al obtener la biblioteca: ${response.code()}")
        }
    }
}