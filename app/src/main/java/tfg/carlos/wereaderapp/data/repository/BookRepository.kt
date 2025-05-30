package tfg.carlos.wereaderapp.data.repository

import tfg.carlos.wereaderapp.data.remote.datasource.BookRemoteDataSource

class BookRepository(
    private val bookRemoteDataSource: BookRemoteDataSource,
) {

    /**
     * Obtiene los libros más vendidos.
     */
    suspend fun getBestsellersBooks() = bookRemoteDataSource.getBestsellersBooks()

    /**
     * Obtiene los nuevos lanzamientos de libros.
     */
    suspend fun getNewReleasesBooks() = bookRemoteDataSource.getNewReleasesBooks()

    /**
     * Obtiene los libros recomendados.
     */
    suspend fun getRecommendedBooks() = bookRemoteDataSource.getRecommendedBooks()

    /**
     * Obtiene un libro por su ID.
     */
    suspend fun getStoreBookById(id: String) = bookRemoteDataSource.getBookById(id)

    /**
     * Busca libros por una consulta.
     */
    suspend fun searchBooks(query: String) = bookRemoteDataSource.searchBooks(query)
}