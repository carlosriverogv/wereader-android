package tfg.carlos.wereaderapp.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import tfg.carlos.wereaderapp.data.model.library.AddBookRequest
import tfg.carlos.wereaderapp.data.model.library.AddBookResponse
import tfg.carlos.wereaderapp.data.model.library.LibraryResponse

interface LibraryService {
    @GET("library/mylibrary")
    suspend fun getAuthUserLibrary(): Response<LibraryResponse>

    @PATCH("library/addBook")
    suspend fun addBookToLibrary(@Body dto: AddBookRequest): Response<AddBookResponse>
}