package tfg.carlos.wereaderapp.ui.profile.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.model.user.User
import tfg.carlos.wereaderapp.databinding.ItemFriendBinding
import tfg.carlos.wereaderapp.ui.avatar.AvatarProvider.getAvatarById

enum class FriendshipAdapterMode {
    FRIEND_LIST,
    SEARCH
}

class FriendshipAdapter(
    private val mode: FriendshipAdapterMode,
    val onClickFriendOptionsButton: (friend: User, position: Int) -> Unit,
    //val onLongClickBookItem: (book: UserFriendshipsResponseItem, position: Int) -> Unit,
) : ListAdapter<User, FriendshipAdapter.FriendsViewHolder>(
    FriendsItemDiffCallback()
) {

    private val sessionManager by lazy {
        WeReaderApplication.sessionManager
    }

    inner class FriendsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val bind = ItemFriendBinding.bind(view)

        fun bind(friendItem: User) {
            bind.apply {
                friendNameText.text = itemView.context.getString(
                    R.string.profile_full_name,
                    friendItem.name, friendItem.lastname
                )
                friendTagText.text = friendItem.tag

                loadAvatarImage(friendAvatarImage, friendItem.avatar)

                friendOptionsButton.setOnClickListener {
                    // Se pasa el id del amigo y la posición del item seleccionado
                    onClickFriendOptionsButton(friendItem, bindingAdapterPosition)
                }

                if (sessionManager.isSharingLibrary()
                    && sessionManager.getSharedUserId() == friendItem.id) {
                    friendSharingImage.visibility = View.VISIBLE
                } else {
                    friendSharingImage.visibility = View.GONE
                }

                if (mode == FriendshipAdapterMode.FRIEND_LIST) {
                    friendOptionsButton.setImageResource(R.drawable.baseline_more_vert_24)
                } else {
                    friendOptionsButton.setImageResource(R.drawable.round_person_add_24)
                }

                /*itemView.setOnLongClickListener {
                    // Se pasa el id del amigo y la posición del item seleccionado
                    onLongClickBookItem(friendItem, bindingAdapterPosition)
                    true
                }*/
            }
        }
    }

    /**
     * Carga la imagen del avatar en el ImageView proporcionado.
     * Utiliza Glide para cargar la imagen y aplicar un recorte circular.
     */
    private fun loadAvatarImage(imageView: ImageView, avatarId: Int) {
        val avatar = getAvatarById(avatarId)
        avatar?.let {
            Glide.with(imageView.context)
                .load(it.drawableRes)
                .transform(CircleCrop())
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        return FriendsViewHolder(
            ItemFriendBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).root
        )
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val friendItem = getItem(position)
        holder.bind(friendItem)
    }
}

class FriendsItemDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(
        oldItem: User,
        newItem: User
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: User,
        newItem: User
    ): Boolean {
        return oldItem == newItem
    }

}
