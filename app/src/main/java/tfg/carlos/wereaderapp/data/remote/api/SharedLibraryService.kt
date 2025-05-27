package tfg.carlos.wereaderapp.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import tfg.carlos.wereaderapp.data.model.sharedlibrary.CreateSharedLibraryRequest
import tfg.carlos.wereaderapp.data.model.sharedlibrary.CreateSharedLibraryResponse
import tfg.carlos.wereaderapp.data.model.sharedlibrary.DeleteSharedLibraryRequest
import tfg.carlos.wereaderapp.data.model.sharedlibrary.DeleteSharedLibraryResponse
import tfg.carlos.wereaderapp.data.model.sharedlibrary.SharedLibraryResponse
import tfg.carlos.wereaderapp.data.model.sharedlibrary.SharedLibraryWrapperResponse

interface SharedLibraryService {
    // Obtiene las bibliotecas compartidas con el usuario autenticado
    @GET("/sharedLibrary/sharedWithMe")
    suspend fun getSharedLibraryWithMe(): Response<SharedLibraryResponse>

    // Obtiene las bibliotecas compartidas por el usuario autenticado
    @GET("/sharedLibrary/sharedByMe")
    suspend fun getSharedLibraryByMe(): Response<SharedLibraryWrapperResponse>

    // Crea una nueva biblioteca compartida con un amigo
    @POST("/sharedLibrary")
    suspend fun shareMyLibrary(@Body request: CreateSharedLibraryRequest)
    : Response<CreateSharedLibraryResponse>

    // Elimina una biblioteca compartida por el usuario autenticado
    @POST("/sharedLibrary/stopSharingMyLibrary")
    suspend fun stopSharingMyLibrary(@Body request: DeleteSharedLibraryRequest)
    : Response<DeleteSharedLibraryResponse>
}