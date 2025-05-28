package tfg.carlos.wereaderapp.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import tfg.carlos.wereaderapp.data.model.book.BookList

interface BookService {
    @GET("book/bestsellers")
    suspend fun getBestsellersBooks(): Response<BookList>

    @GET("book/newReleases")
    suspend fun getNewReleasesBooks(): Response<BookList>
}