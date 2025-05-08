package tfg.carlos.wereaderapp.ui.library.fragments.library

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.data.entity.BookEntity
import tfg.carlos.wereaderapp.databinding.ItemBookBinding

class BooksAdapter(
    val onClickBookItem: (idBook: String, position: Int) -> Unit,
    val onLongClickBookItem: (idBook: String, position: Int, isPending: Boolean) -> Unit,
) : ListAdapter<BookEntity, BooksAdapter.BooksViewHolder>(BookItemDiffCallback()) {
    // Se inicializa Firebase Storage
    private val storage = Firebase.storage

    inner class BooksViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val bind = ItemBookBinding.bind(view)

        fun bind(bookEntity: BookEntity) {
            bind.apply {
                bind.titleBook.text = bookEntity.title
                bind.authorName.text = bookEntity.author

                // Se obtiene la referencia de Firebase de la portada del libro (getReference)
                storage.getReference(bookEntity.coverUrl).downloadUrl.addOnSuccessListener { uri ->
                    if (itemView.isAttachedToWindow) {
                        Glide.with(itemView.context)
                            .load(uri)
                            .placeholder(R.drawable.ic_book_placeholder)
                            .transform(FitCenter(), RoundedCorners(8))
                            .into(bind.coverImage)
                    }
                }.addOnFailureListener {
                    // Manejar el error al obtener la URL de descarga
                    Log.e("BooksAdapter", "Error al obtener la URL de descarga: ${it.message}")
                }

                itemView.setOnClickListener {
                    // Se pasa el id del book y la posición del item seleccionado
                    onClickBookItem(bookEntity.id, adapterPosition)
                    Log.d("BooksAdapter", "Item clicked: ${bookEntity.id}")
                }

                itemView.setOnLongClickListener {
                    // Se pasa el id del book y la posición del item seleccionado
                    onLongClickBookItem(bookEntity.id, adapterPosition, bookEntity.isPending)
                    Log.d("BooksAdapter", "Item long clicked: ${bookEntity.id}")
                    true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        return BooksViewHolder(
            ItemBookBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).root
        )
    }

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
        val bookItem = getItem(position)
        holder.bind(bookItem)
    }
}

class BookItemDiffCallback : DiffUtil.ItemCallback<BookEntity>() {
    override fun areItemsTheSame(oldItem: BookEntity, newItem: BookEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BookEntity, newItem: BookEntity): Boolean {
        return oldItem == newItem
    }
}

