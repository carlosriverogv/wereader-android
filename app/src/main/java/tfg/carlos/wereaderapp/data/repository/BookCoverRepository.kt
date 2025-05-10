package tfg.carlos.wereaderapp.data.repository

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class BookCoverRepository(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    private val urlCache = mutableMapOf<String, String>()

    suspend fun getDownloadUrl(coverPath: String): String {
        urlCache[coverPath]?.let { return it }

        val ref = storage.getReference(coverPath)
        val uri = ref.downloadUrl.await()
        val url = uri.toString()

        urlCache[coverPath] = url
        return url
    }
}