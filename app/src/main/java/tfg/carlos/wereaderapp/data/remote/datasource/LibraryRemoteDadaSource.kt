package tfg.carlos.wereaderapp.data.remote.datasource

import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import tfg.carlos.wereaderapp.data.model.library.LibraryResponse
import tfg.carlos.wereaderapp.data.model.sharedlibrary.SharedLibraryResponse
import tfg.carlos.wereaderapp.data.remote.Retrofit2Api

class LibraryRemoteDadaSource {

    // TODO: Revisar NO UTILIZAR flow y usar unicamente suspend
    suspend fun getAuthUserLibrary() = flow<LibraryResponse> {
        val response = Retrofit2Api.libraryApi.getAuthUserLibrary()
        if (response.isSuccessful) {
            emit(response.body()!!)
        } else {
            throw Exception("Error al obtener la biblioteca: ${response.code()}")
        }
    }

    // TODO: Revisar NO UTILIZAR flow y usar unicamente suspend
    // TODO: Revisar ELSE
    suspend fun getSharedLibrary() = flow<SharedLibraryResponse> {
        val response = Retrofit2Api.sharedLibraryApi.getSharedLibraryWithMe()
        if (response.isSuccessful) {
            emit(response.body()!!)
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody).getString("message")
            } catch (e: Exception) {
                "Error al obtener la biblioteca compartida: ${response.code()}"
            }
            throw Exception(errorMessage)
        }
    }
}