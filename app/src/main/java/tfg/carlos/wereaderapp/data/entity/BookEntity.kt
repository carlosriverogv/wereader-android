package tfg.carlos.wereaderapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val coverUrl: String,
    val epubUrl: String,
    val synopsis: String,
    val datePublished: String,
    val price: Double,
    val downloads: Int,
    val isbn: String,
    val shareable: Boolean,
    val genre: String,
    val dateCreation: String,
    val v : Int
)
