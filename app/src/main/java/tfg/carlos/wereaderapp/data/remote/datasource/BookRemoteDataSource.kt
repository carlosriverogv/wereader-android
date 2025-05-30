package tfg.carlos.wereaderapp.data.remote.datasource

import tfg.carlos.wereaderapp.data.model.book.BookItem
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

    /**
     * Obtiene los libros recomendados.
     */
    suspend fun getRecommendedBooks(): BookList {
        val response = Retrofit2Api.bookApi.getRecommendedBooks()
        if (response.isSuccessful) {
            return response.body() ?: BookList()
        } else {
            throw Exception("Error al obtener los libros recomendados: ${response.code()}")
        }
    }

    /**
     * Obtiene un libro por su ID.
     */
    suspend fun getBookById(id: String): BookItem? {
        val response = Retrofit2Api.bookApi.getBookById(id)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al obtener el libro por ID: ${response.code()}")

        }
    }

    /**
     * Busca libros por una consulta.
     */
    suspend fun searchBooks(query: String): BookList {
        val response = Retrofit2Api.bookApi.searchBooks(query)
        if (response.isSuccessful) {
            return response.body() ?: BookList()
        } else {
            throw Exception("Error al buscar libros: ${response.code()}")
        }
    }
}