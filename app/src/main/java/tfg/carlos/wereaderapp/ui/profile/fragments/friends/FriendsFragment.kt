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

    private val sessionManager by lazy {
        WeReaderApplication.sessionManager
    }

    private val friendshipViewModel: FriendsViewModel by viewModels {
        // Data source y repository para la biblioteca
        val db = (requireActivity().application as WeReaderApplication).weReaderDB
        val libraryLocalDataSource = LibraryLocalDataSource(db.bookDao())
        val libraryRemoteDadaSource = LibraryRemoteDadaSource()
        val libraryRepository = LibraryRepository(libraryRemoteDadaSource, libraryLocalDataSource)

        // Data source y repository para friendships
        val friendshipRemoteDadaSource = FriendshipRemoteDataSource()
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

    /**
     * Muestra un menú de opciones para el amigo seleccionado.
     * Este método se encarga de mostrar un menú contextual con las opciones disponibles
     * para el amigo seleccionado en la lista de amigos.
     *
     * @param recyclerView El RecyclerView donde se encuentra el amigo seleccionado.
     * @param friend El objeto UserFriendshipsResponseItem que representa al amigo seleccionado.
     * @param position La posición del amigo en la lista del RecyclerView.
     */
    private fun showFriendOptionsMenu(
        recyclerView: RecyclerView, friend: UserFriendshipsResponseItem, position: Int) {
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position) ?: return
        val anchorView = viewHolder.itemView.findViewById<View>(R.id.friendOptionsButton)
        FriendMenuHandler.show(
            context = requireContext(),
            anchorView = anchorView,
            friend = friend,
            onToggleShare = {
                // Se obtiene el estado de compartir la biblioteca y el id del amigo
                val isSharing = sessionManager.isSharingLibrary()
                val friendUserId = sessionManager.getSharedUserId()
                val isSharingWithThisFriend = isSharing && (friend.id == friendUserId)

                if (isSharingWithThisFriend) {
                    // Si ya se está compartiendo la biblioteca con este amigo, se detiene el compartir
                    stopSharingMyLibrary(friend)
                } else {
                    // Si no se está compartiendo, se muestra el diálogo para compartir la biblioteca
                    shareLibraryWithFriend(friend)
                }
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

    /**
     * Método para compartir la biblioteca con un amigo.
     * Muestra un diálogo de confirmación antes de proceder con el compartir.
     *
     * @param friend El amigo con el que se desea compartir la biblioteca.
     */
    private fun shareLibraryWithFriend(friend: UserFriendshipsResponseItem) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.alert_dialog_share_library_title))
            .setMessage(getString(R.string.alert_dialog_share_library_message, friend.tag))
            .setPositiveButton(getString(R.string.alert_dialog_share_library_positive)) { _, _ ->
                // Se comparte la biblioteca con el amigo
                friendshipViewModel.shareMyLibraryWithFriend(friend.id)

                // Se observa el resultado de la operación de compartir
                checkShareSuccess(friend.id)
            }
            .setNegativeButton(getString(R.string.alert_dialog_share_library_negative), null)
            .show()
    }

    /**
     * Método para detener el compartir la biblioteca con un amigo.
     * TODO: Pasar al ViewModel y eliminar este método del Fragment.
     */
    private fun stopSharingMyLibrary(friend: UserFriendshipsResponseItem) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.alert_dialog_stop_sharing_title))
            .setMessage(getString(R.string.alert_dialog_stop_sharing_message, friend.tag))
            .setPositiveButton(getString(R.string.alert_dialog_stop_sharing_positive)) { _, _ ->
                // TODO: Se elimina la biblioteca compartida con el amigo
                friendshipViewModel.stopSharingMyLibrary(friend.id)

                // Se resetea el cache de la biblioteca compartida
                sessionManager.clearSharingLibrary()

                // Se notifica al adapter que se ha actualizado el estado de compartir
                adapter.notifyItemChanged(clickedItemPosition)
            }
            .setNegativeButton(getString(R.string.alert_dialog_stop_sharing_negative), null)
            .show()
    }

    /**
     * Método para verificar si la operación de compartir fue exitosa.
     * Observa el LiveData shareSuccess del ViewModel y actualiza el estado de compartir en SharedPreferences.
     * TODO: Pasar al ViewModel y eliminar este método del Fragment.
     * @param friendId El ID del amigo con el que se intentó compartir la biblioteca.
     */
    private fun checkShareSuccess(friendId: String) {
        friendshipViewModel.shareSuccess.observe(viewLifecycleOwner) { success ->
            // Si la operación fue exitosa, se actualiza el estado de compartir en SharedReferences
            if (success) {
                sessionManager.saveSharingLibrary(
                    isSharing = true,
                    friendUserId = friendId
                )
                adapter.notifyItemChanged(clickedItemPosition)
            }
        }
    }
}