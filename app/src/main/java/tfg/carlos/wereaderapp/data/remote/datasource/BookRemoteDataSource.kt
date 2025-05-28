package tfg.carlos.wereaderapp.data.remote.datasource

import tfg.carlos.wereaderapp.data.model.book.BookList
import tfg.carlos.wereaderapp.data.remote.Retrofit2Api

class BookRemoteDataSource {
    /**
     * Obtiene los libros más vendidos.
     */
    suspend fun getBestsellersBooks(): BookList {
        val response = Retrofit2Api.bookApi.getBestsellersBooks()
        if (response.isSuccessful) {
            return response.body() ?: BookList()
        } else {
            throw Exception("Error al obtener los libros más vendidos: ${response.code()}")
        }
    }

    /**
     * Obtiene los nuevos lanzamientos de libros.
     */
    suspend fun getNewReleasesBooks(): BookList {
        val response = Retrofit2Api.bookApi.getNewReleasesBooks()
        if (response.isSuccessful) {
            return response.body() ?: BookList()
        } else {
            throw Exception("Error al obtener los nuevos lanzamientos de libros: ${response.code()}")
        }
    }
}