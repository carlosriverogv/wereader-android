package tfg.carlos.wereaderapp.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import tfg.carlos.wereaderapp.data.model.library.LibraryResponse

interface LibraryService {
    @GET("library/mylibrary")
    suspend fun getMyLibrary(): Response<LibraryResponse>
}