package tfg.carlos.wereaderapp.ui.library.fragments.book

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
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.data.entity.BookEntity
import tfg.carlos.wereaderapp.databinding.ItemBookBinding

class BooksAdapter(
    val onClickBookItem: (idBook: String, position: Int) -> Unit,
) : ListAdapter<BookEntity, BooksAdapter.BooksViewHolder>(BookItemDiffCallback()) {

    inner class BooksViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val bind = ItemBookBinding.bind(view)

        fun bind(bookEntity: BookEntity) {
            bind.apply {
                bind.titleBook.text = bookEntity.title
                bind.authorName.text = bookEntity.author

                Glide.with(itemView)
                    .load(R.drawable.ic_book_placeholder)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .transform(FitCenter(), RoundedCorners(8))
                    .into(bind.coverImage)

                itemView.setOnClickListener {
                    // Se pasa el id del show y la posición del item seleccionado
                    onClickBookItem(bookEntity.id, adapterPosition)
                    Log.d("BooksAdapter", "Item clicked: ${bookEntity.id}")
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

