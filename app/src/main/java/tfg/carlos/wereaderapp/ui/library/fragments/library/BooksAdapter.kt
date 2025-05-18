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
    val onClickBookItem: (book: BookEntity, position: Int) -> Unit,
    val onLongClickBookItem: (idBook: String, position: Int, isPending: Boolean) -> Unit,
) : ListAdapter<BookEntity, BooksAdapter.BooksViewHolder>(BookItemDiffCallback()) {
    // Se inicializa Firebase Storage
    private val storage = Firebase.storage

    companion object {
        private val downloadUrlCache = mutableMapOf<String, String>()
    }

    inner class BooksViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val bind = ItemBookBinding.bind(view)

        fun bind(bookEntity: BookEntity) {
            bind.apply {
                val cachedUrl = downloadUrlCache[bookEntity.coverUrl]
                if (cachedUrl != null) {
                    loadImage(cachedUrl)
                } else {
                    storage.getReference(bookEntity.coverUrl).downloadUrl.addOnSuccessListener { uri ->
                        downloadUrlCache[bookEntity.coverUrl] = uri.toString()
                        if (itemView.isAttachedToWindow) {
                            loadImage(uri.toString())
                        }
                    }
                }

                titleBook.text = bookEntity.title
                authorName.text = bookEntity.author
                val progress = bookEntity.readingProgress.toInt()
                progressPercent.text = progressPercent.context.getString(
                    R.string.item_book_progress, progress)

                itemView.setOnClickListener {
                    // Se pasa el id del book y la posición del item seleccionado
                    onClickBookItem(bookEntity, bindingAdapterPosition)
                    Log.d("BooksAdapter", "Item clicked: ${bookEntity.id}")
                }

                itemView.setOnLongClickListener {
                    // Se pasa el id del book y la posición del item seleccionado
                    onLongClickBookItem(bookEntity.id, bindingAdapterPosition, bookEntity.isPending)
                    Log.d("BooksAdapter", "Item long clicked: ${bookEntity.id}")
                    true
                }
            }
        }

        private fun loadImage(url: String) {
            Glide.with(itemView.context)
                .load(url)
                .placeholder(R.drawable.ic_book_placeholder)
                .transform(FitCenter(), RoundedCorners(8))
                .into(bind.coverImage)
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

