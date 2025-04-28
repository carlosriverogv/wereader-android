package tfg.carlos.wereaderapp.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import tfg.carlos.wereaderapp.data.model.sharedlibrary.SharedLibraryResponse

interface SharedLibraryService {
    @GET("/sharedlibrary/sharedWithMe")
    suspend fun getSharedLibraryWithMe(): Response<SharedLibraryResponse>
}