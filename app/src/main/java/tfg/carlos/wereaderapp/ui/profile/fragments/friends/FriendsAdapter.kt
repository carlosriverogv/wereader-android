package tfg.carlos.wereaderapp.ui.profile.fragments.friends

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
import tfg.carlos.wereaderapp.data.model.friendship.UserFriendshipsResponseItem
import tfg.carlos.wereaderapp.databinding.ItemFriendBinding
import tfg.carlos.wereaderapp.ui.avatar.AvatarProvider.getAvatarById

class FriendsAdapter(
    val onClickFriendOptionsButton: (friend: UserFriendshipsResponseItem, position: Int) -> Unit,
    //val onLongClickBookItem: (book: UserFriendshipsResponseItem, position: Int) -> Unit,
) : ListAdapter<UserFriendshipsResponseItem, FriendsAdapter.FriendsViewHolder>(FriendsItemDiffCallback()) {

    inner class FriendsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val bind = ItemFriendBinding.bind(view)

        fun bind(friendItem: UserFriendshipsResponseItem) {
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

                /*itemView.setOnLongClickListener {
                    // Se pasa el id del amigo y la posición del item seleccionado
                    onLongClickBookItem(friendItem, bindingAdapterPosition)
                    true
                }*/
            }
        }
    }

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

class FriendsItemDiffCallback : DiffUtil.ItemCallback<UserFriendshipsResponseItem>() {
    override fun areItemsTheSame(
        oldItem: UserFriendshipsResponseItem,
        newItem: UserFriendshipsResponseItem
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: UserFriendshipsResponseItem,
        newItem: UserFriendshipsResponseItem
    ): Boolean {
        return oldItem == newItem
    }

}
