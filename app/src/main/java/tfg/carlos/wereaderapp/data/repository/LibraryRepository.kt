package tfg.carlos.wereaderapp.data.repository

import tfg.carlos.wereaderapp.data.remote.datasource.LibraryRemoteDadaSource

class LibraryRepository(val dadaSource: LibraryRemoteDadaSource) {
    // API Methods
    suspend fun getMyLibrary() = dadaSource.getMyLibrary()

    // ROOM Methods
}