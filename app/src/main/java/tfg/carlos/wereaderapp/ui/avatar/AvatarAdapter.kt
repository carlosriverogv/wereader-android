package tfg.carlos.wereaderapp.ui.avatar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import tfg.carlos.wereaderapp.data.model.user.Avatar
import tfg.carlos.wereaderapp.databinding.ItemAvatarBinding

class AvatarAdapter(
    private val avatars: List<Avatar>,
    private val onAvatarSelected: (Int) -> Unit
) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    private var selectedPosition = -1

    inner class AvatarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemAvatarBinding.bind(view)

        fun bind(avatar: Avatar, isSelected: Boolean) {
            binding.avatarImage.setImageResource(avatar.drawableRes)

            Glide.with(itemView.context)
                .load(avatar.drawableRes)
                .transform(CircleCrop())
                .into(binding.avatarImage)

            binding.selectionBorder.visibility = if (isSelected) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = bindingAdapterPosition
                onAvatarSelected(avatar.id)
                notifyItemChanged(previousPosition)
                notifyItemChanged(bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        return AvatarViewHolder(
            ItemAvatarBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).root
        )
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        val isSelected = position == selectedPosition
        holder.bind(avatars[position], isSelected)
    }

    override fun getItemCount(): Int = avatars.size
}