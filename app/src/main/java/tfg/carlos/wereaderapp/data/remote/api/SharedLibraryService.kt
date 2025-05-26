package tfg.carlos.wereaderapp.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import tfg.carlos.wereaderapp.data.model.auth.RegisterRequest
import tfg.carlos.wereaderapp.data.model.sharedlibrary.CreateSharedLibraryRequest
import tfg.carlos.wereaderapp.data.model.sharedlibrary.CreateSharedLibraryResponse
import tfg.carlos.wereaderapp.data.model.sharedlibrary.SharedLibraryResponse

interface SharedLibraryService {
    @GET("/sharedlibrary/sharedWithMe")
    suspend fun getSharedLibraryWithMe(): Response<SharedLibraryResponse>

    @GET("/sharedlibrary/sharedByMe")
    suspend fun getSharedLibraryByMe(): Response<SharedLibraryResponse>

    @POST("/sharedlibrary")
    suspend fun shareMyLibrary(@Body request: CreateSharedLibraryRequest)
    : Response<CreateSharedLibraryResponse>
}