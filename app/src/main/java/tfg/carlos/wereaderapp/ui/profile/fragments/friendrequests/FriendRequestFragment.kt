package tfg.carlos.wereaderapp.ui.profile.fragments.friendrequests

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.data.model.friendship.UserFriendshipsResponseItem
import tfg.carlos.wereaderapp.data.remote.datasource.FriendshipRemoteDataSource
import tfg.carlos.wereaderapp.data.repository.FriendshipRepository
import tfg.carlos.wereaderapp.databinding.FragmentFriendRequestBinding
import tfg.carlos.wereaderapp.ui.profile.ProfileViewModel
import tfg.carlos.wereaderapp.ui.profile.fragments.FriendshipAdapter
import tfg.carlos.wereaderapp.ui.profile.fragments.friends.FriendsViewModel
import tfg.carlos.wereaderapp.utils.FriendRequestsMenuHandler

class FriendRequestFragment : Fragment() {
    private var _binding: FragmentFriendRequestBinding? = null
    private val binding get() = _binding!!
    private var clickedItemPosition: Int = RecyclerView.NO_POSITION

    private val sessionManager by lazy {
        tfg.carlos.wereaderapp.WeReaderApplication.sessionManager
    }

    private val friendRequestsViewModel: FriendRequestsViewModel by viewModels {
        // Data source y repository para friendships
        val friendshipRemoteDadaSource = FriendshipRemoteDataSource()
        val friendshipRepository = FriendshipRepository(
            friendshipRemoteDadaSource)
        FriendRequestsViewModelFactory(friendshipRepository)
    }

    private val adapter = FriendshipAdapter(
        onClickFriendOptionsButton = { friend: UserFriendshipsResponseItem, position: Int ->
            clickedItemPosition = position
            showFriendRequestOptionsMenu(binding.friendRequestsRecyclerView, friend, position)
        }
        //, onLongClickBookItem = { book, position -> /* TODO: Implement long click action */ }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFriendRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Se establece el adaptador del RecyclerView
        binding.friendRequestsRecyclerView.adapter = adapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            friendRequestsViewModel.reloadFriendRequests()
        }

        // Se obtienen las solicitudes de amistad
        getFriendRequests()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Obtiene las solicitudes de amistad del ViewModel y las muestra en el RecyclerView.
     * Si no hay solicitudes, se muestra un mensaje indicando que no hay amistades.
     */
    private fun getFriendRequests() {
        // Limpia la lista anterior antes de volver a cargar (esto está bien)
        adapter.submitList(null)

        friendRequestsViewModel.friendRequests.observe(viewLifecycleOwner) { friendRequests ->
            binding.swipeRefreshLayout.isRefreshing = false

            if (friendRequests.isNotEmpty()) {
                binding.friendRequestsRecyclerView.visibility = View.VISIBLE
                binding.tvFriendRequestsEmpty.visibility = View.GONE
                adapter.submitList(friendRequests)
            } else {
                binding.friendRequestsRecyclerView.visibility = View.GONE
                binding.tvFriendRequestsEmpty.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Muestra un menú de opciones para aceptar o rechazar una solicitud de amistad.
     * Utiliza el método showFriendRequestOptionsMenu del FriendRequestsMenuHandler.
     * @param recyclerView El RecyclerView donde se encuentra la solicitud de amistad.
     * @param friend El objeto UserFriendshipsResponseItem que representa la solicitud de amistad.
     * @param position La posición del elemento en el RecyclerView.
     */
    private fun showFriendRequestOptionsMenu(
        recyclerView: RecyclerView, friend: UserFriendshipsResponseItem, position: Int) {
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position) ?: return
        val anchorView = viewHolder.itemView.findViewById<View>(R.id.friendOptionsButton)

        FriendRequestsMenuHandler.show(
            context = requireContext(),
            anchorView = anchorView,
            onAccept = {
                acceptFriendRequest(friend)
                // Se acepta la solicitud de amistad
                //friendRequestsViewModel.acceptFriendRequest(
                    //adapter.currentList[clickedItemPosition].id)
            },
            onReject = {
                rejectFriendRequest(friend)
                // Se rechaza la solicitud de amistad
                //friendRequestsViewModel.rejectFriendRequest(
                    //adapter.currentList[clickedItemPosition].id)
            }
        )
    }

    /**
     * Muestra un diálogo de alerta para aceptar o rechazar una solicitud de amistad.
     * Si se acepta, se llama al método correspondiente del ViewModel.
     * @param friend El objeto UserFriendshipsResponseItem que representa la solicitud de amistad.
     */
    private fun acceptFriendRequest(friend: UserFriendshipsResponseItem) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.alert_dialog_accept_friend_request_title))
            .setMessage(getString(R.string.alert_dialog_accept_friend_request_message, friend.tag))
            .setPositiveButton(getString(R.string.alert_dialog_accept_friend_request_positive)) { _, _ ->
                // Se acepta la solicitud de amistad
                friendRequestsViewModel.acceptFriendRequest(friend.id)
                // Se observa el resultado de la operación
                //checkShareSuccess(friend.id)
            }
            .setNegativeButton(getString(R.string.alert_dialog_accept_friend_request_negative), null)
            .show()
    }

    /**
     * Muestra un diálogo de alerta para rechazar una solicitud de amistad.
     * Si se rechaza, se llama al método correspondiente del ViewModel.
     * @param friend El objeto UserFriendshipsResponseItem que representa la solicitud de amistad.
     */
    private fun rejectFriendRequest(friend: UserFriendshipsResponseItem) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.alert_dialog_reject_friend_request_title))
            .setMessage(getString(R.string.alert_dialog_reject_friend_request_message, friend.tag))
            .setPositiveButton(getString(R.string.alert_dialog_reject_friend_request_positive)) { _, _ ->
                // Se rechaza la solicitud de amistad
                friendRequestsViewModel.rejectFriendRequest(friend.id)
                // Se observa el resultado de la operación
                //checkShareSuccess(friend.id)
            }
            .setNegativeButton(getString(R.string.alert_dialog_reject_friend_request_negative), null)
            .show()
    }
}