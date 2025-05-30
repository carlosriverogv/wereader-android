package tfg.carlos.wereaderapp.ui.discover.search

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
import tfg.carlos.wereaderapp.databinding.ItemBookSearchBinding

class BookSearchAdapter(
    val onClickBookItem: (book: BookItem, position: Int) -> Unit
) : ListAdapter<BookItem, BookSearchAdapter.BookSearchAdapterViewHolder>(BookItemDiffCallback()) {
    private val storage = Firebase.storage

    companion object {
        private val downloadUrlCache = mutableMapOf<String, String>()
    }

    inner class BookSearchAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val bind = ItemBookSearchBinding.bind(view)

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

                bookTitle.text = bookItem.title
                bookAuthor.text = bookItem.author
                bookGenre.text = bookItem.genre
                bookPrice.text = bookPrice.context.getString(
                    R.string.book_detail_price, bookItem.price)

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
                .into(bind.bookCover)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookSearchAdapterViewHolder {
        return BookSearchAdapterViewHolder(
            ItemBookSearchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).root
        )
    }

    override fun onBindViewHolder(holder: BookSearchAdapterViewHolder, position: Int) {
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