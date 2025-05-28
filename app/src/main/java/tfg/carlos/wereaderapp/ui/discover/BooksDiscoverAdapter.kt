package tfg.carlos.wereaderapp.ui.discover

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
import tfg.carlos.wereaderapp.data.model.book.BookItem
import tfg.carlos.wereaderapp.databinding.ItemBookDiscoverBinding

class BooksDiscoverAdapter(
    val onClickBookItem: (book: BookItem, position: Int) -> Unit,
    //val onLongClickBookItem: (book: String, position: Int) -> Unit,
) : ListAdapter<BookItem, BooksDiscoverAdapter.BooksDiscoverViewHolder>(BookItemDiffCallback()) {
    private val storage = Firebase.storage

    companion object {
        private val downloadUrlCache = mutableMapOf<String, String>()
    }

    inner class BooksDiscoverViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val bind = ItemBookDiscoverBinding.bind(view)

        fun bind(bookItem: BookItem) {
            bind.apply {
                val cachedUrl = downloadUrlCache[bookItem.coverUrl]
                if (cachedUrl != null) {
                    loadImage(cachedUrl)
                } else {
                    storage.getReference(bookItem.coverUrl).downloadUrl.addOnSuccessListener { uri ->
                        downloadUrlCache[bookItem.coverUrl] = uri.toString()
                        if (itemView.isAttachedToWindow) {
                            loadImage(uri.toString())
                        }
                    }
                }

                titleBook.text = bookItem.title
                authorName.text = bookItem.author

                itemView.setOnClickListener {
                    // Se pasa el id del book y la posición del item seleccionado
                    onClickBookItem(bookItem, bindingAdapterPosition)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksDiscoverViewHolder {
        return BooksDiscoverViewHolder(
            ItemBookDiscoverBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).root
        )
    }

    override fun onBindViewHolder(holder: BooksDiscoverViewHolder, position: Int) {
        val book = getItem(position)
        holder.bind(book)
    }
}

class BookItemDiffCallback : DiffUtil.ItemCallback<BookItem>() {
    override fun areItemsTheSame(oldItem: BookItem, newItem: BookItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BookItem, newItem: BookItem): Boolean {
        return oldItem == newItem
    }
}