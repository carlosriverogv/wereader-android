package tfg.carlos.wereaderapp.data.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "books",
    primaryKeys = ["id", "idUser"],
    indices = [Index(value = ["idUser"])]
)

data class BookEntity(
    val id: String,
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
    val v : Int,

    // Campos locales ROOM:
    val isPending: Boolean = false,
    val isReading: Boolean = false,
    val readingProgress: Double = 0.0, // Porcentaje de lectura (0-100)
    val mine: Boolean = true,
    val idUser: String,
    val lastLocator: String? = null, // Última posición de lectura (JSON de Readium) (null si no se ha leído el libro aún)
)
