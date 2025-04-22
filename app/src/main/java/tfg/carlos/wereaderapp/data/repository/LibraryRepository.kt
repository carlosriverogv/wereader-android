package tfg.carlos.wereaderapp.data.repository

import tfg.carlos.wereaderapp.data.remote.datasource.LibraryRemoteDadaSource

class LibraryRepository(val dadaSource: LibraryRemoteDadaSource) {
    // API Methods
    suspend fun getAuthUserLibrary() = dadaSource.getAuthUserLibrary()

    // ROOM Methods
}