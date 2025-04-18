package tfg.carlos.wereaderapp.ui.auth.register

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.databinding.ItemAvatarBinding

class AvatarAdapter(
    private val avatars: List<Int>,
    private val onAvatarSelected: (Int) -> Unit
) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    private var selectedPosition = -1

    inner class AvatarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemAvatarBinding.bind(view)

        fun bind(avatar: Int, isSelected: Boolean) {
            binding.avatarImage.setImageResource(avatar)

            Glide.with(itemView.context)
                .load(avatar)
                .transform(CircleCrop())
                .into(binding.avatarImage)

            // Aplica el fondo solo si está seleccionado
            if (isSelected) {
                binding.avatarImage.setBackgroundResource(R.drawable.avatar_selected_border)
            } else {
                binding.avatarImage.background = null
            }

            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                onAvatarSelected(avatar)
                notifyItemChanged(previousPosition)
                notifyItemChanged(adapterPosition)
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