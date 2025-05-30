package tfg.carlos.wereaderapp.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import tfg.carlos.wereaderapp.data.model.book.BookItem
import tfg.carlos.wereaderapp.data.model.book.BookList

interface BookService {
    @GET("book/bestsellers")
    suspend fun getBestsellersBooks(): Response<BookList>

    @GET("book/newReleases")
    suspend fun getNewReleasesBooks(): Response<BookList>

    @GET("book/recommended")
    suspend fun getRecommendedBooks(): Response<BookList>

    @GET("book/{id}")
    suspend fun getBookById(@Path("id") id: String): Response<BookItem>

    @GET("book/search")
    suspend fun searchBooks(@Query("query") query: String, ): Response<BookList>
}