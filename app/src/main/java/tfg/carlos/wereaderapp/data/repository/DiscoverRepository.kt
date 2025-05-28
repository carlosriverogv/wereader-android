package tfg.carlos.wereaderapp.data.repository

import tfg.carlos.wereaderapp.data.remote.datasource.BookRemoteDataSource

class DiscoverRepository(
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
}