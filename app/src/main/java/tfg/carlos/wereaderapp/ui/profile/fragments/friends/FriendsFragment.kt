package tfg.carlos.wereaderapp.ui.profile.fragments.friends

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.local.datasource.LibraryLocalDataSource
import tfg.carlos.wereaderapp.data.model.friendship.UserFriendshipsResponseItem
import tfg.carlos.wereaderapp.data.remote.datasource.AuthRemoteDataSource
import tfg.carlos.wereaderapp.data.remote.datasource.FriendshipRemoteDataSource
import tfg.carlos.wereaderapp.data.remote.datasource.LibraryRemoteDadaSource
import tfg.carlos.wereaderapp.data.repository.AuthRepository
import tfg.carlos.wereaderapp.data.repository.FriendshipRepository
import tfg.carlos.wereaderapp.data.repository.LibraryRepository
import tfg.carlos.wereaderapp.databinding.FragmentFriendsBinding
import tfg.carlos.wereaderapp.databinding.FragmentLibraryBinding
import tfg.carlos.wereaderapp.ui.profile.ProfileViewModel
import tfg.carlos.wereaderapp.ui.profile.ProfileViewModelFactory
import tfg.carlos.wereaderapp.utils.FriendMenuHandler

class FriendsFragment : Fragment() {
    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!
    private var clickedItemPosition: Int = RecyclerView.NO_POSITION

    private val friendshipViewModel: FriendsViewModel by viewModels {
        val friendshipRemoteDadaSource = FriendshipRemoteDataSource()
        val libraryRemoteDadaSource = LibraryRemoteDadaSource()
        val libraryLocalDataSource = LibraryLocalDataSource((requireActivity().application as WeReaderApplication).weReaderDB.bookDao())
        val libraryRepository = LibraryRepository(
            libraryRemoteDadaSource,
            libraryLocalDataSource
        )
        val friendshipRepository = FriendshipRepository(
            friendshipRemoteDadaSource)
        FriendsViewModelFactory(friendshipRepository, libraryRepository)
    }

    private val adapter = FriendsAdapter(
        onClickFriendOptionsButton = { friend: UserFriendshipsResponseItem, position: Int ->
            clickedItemPosition = position
            showFriendOptionsMenu(binding.friendsRecyclerView, friend, position)
        }
        //, onLongClickBookItem = { book, position -> /* TODO: Implement long click action */ }
    )

    private fun showFriendOptionsMenu(
        recyclerView: RecyclerView, friend: UserFriendshipsResponseItem, position: Int) {
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position) ?: return
        val anchorView = viewHolder.itemView.findViewById<View>(R.id.friendOptionsButton)
        FriendMenuHandler.show(
            context = requireContext(),
            anchorView = anchorView,
            friend = friend,
            onToggleShare = {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.alert_dialog_share_library_title))
                    .setMessage(getString(R.string.alert_dialog_share_library_message, friend.tag))
                    .setPositiveButton(getString(R.string.alert_dialog_share_library_positive)) { _, _ ->
                        friendshipViewModel.shareMyLibraryWithFriend(friend.id)
                    }
                    .setNegativeButton(getString(R.string.alert_dialog_share_library_negative), null)
                    .show()
            },
            onDeleteFriend = {
                // TODO: Se elimina el amigo y se actualiza la lista

            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Se establece el adaptador del RecyclerView
        binding.friendsRecyclerView.adapter = adapter

        // Se obtiene la lista de amigos del ViewModel
        getFriends()

        // Se observa el mensaje de error del ViewModel
        showError()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Método para obtener la lista de amigos del ViewModel.
     * Aquí se debería implementar la lógica para interactuar con el ViewModel
     * y obtener los datos necesarios.
     */
    private fun getFriends() {
        adapter.submitList(null)
        friendshipViewModel.friends.observe(viewLifecycleOwner) { friendList ->
            /** Actualiza el RecyclerView con la lista de amigos obtenida del ViewModel.
             * Si la lista está vacía, se muestra un mensaje de "Actualmente no hay amistades".
             * Si la lista contiene amigos, se actualiza el RecyclerView con los datos.
             */
            if (friendList.isEmpty()) {
                binding.friendsRecyclerView.visibility = View.GONE
                binding.tvFriendsEmpty.visibility = View.VISIBLE
            } else {
                binding.friendsRecyclerView.visibility = View.VISIBLE
                binding.tvFriendsEmpty.visibility = View.GONE
                adapter.submitList(friendList)  // friendList ya es un List<UserFriendshipsResponseItem>
            }
        }
    }

    /**
     * Método para mostrar un mensaje de error utilizando Snackbar.
     * Observa el mensaje de error del ViewModel y lo muestra si está presente.
     */
    private fun showError() {
        friendshipViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG)
                    .show()
                // Limpiar el mensaje después de mostrarlo
                friendshipViewModel.clearErrorMessage()
            }
        }
    }
}