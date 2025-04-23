package tfg.carlos.wereaderapp.data.remote.datasource

import kotlinx.coroutines.flow.flow
import tfg.carlos.wereaderapp.data.model.library.LibraryResponse
import tfg.carlos.wereaderapp.data.remote.Retrofit2Api

class LibraryRemoteDadaSource {
    suspend fun getAuthUserLibrary() = flow<LibraryResponse> {
        val response = Retrofit2Api.libraryApi.getAuthUserLibrary()
        if (response.isSuccessful) {
            emit(response.body()!!)
        } else {
            throw Exception("Error al obtener la biblioteca: ${response.code()}")
        }
    }
}