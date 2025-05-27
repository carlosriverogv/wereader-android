package tfg.carlos.wereaderapp.ui.profile.fragments.friendrequests

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import tfg.carlos.wereaderapp.data.model.friendship.UserFriendshipsResponseItem
import tfg.carlos.wereaderapp.data.remote.datasource.FriendshipRemoteDataSource
import tfg.carlos.wereaderapp.data.repository.FriendshipRepository
import tfg.carlos.wereaderapp.databinding.FragmentFriendRequestBinding
import tfg.carlos.wereaderapp.ui.profile.fragments.FriendshipAdapter

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
            //showFriendOptionsMenu(binding.friendRequestsRecyclerView, friend, position)
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

        // Se obtienen las solicitudes de amistad
        getFriendRequests()
    }

    /**
     * Obtiene las solicitudes de amistad del ViewModel y las muestra en el RecyclerView.
     * Si no hay solicitudes, se muestra un mensaje indicando que no hay amistades.
     */
    private fun getFriendRequests() {
        adapter.submitList(null)
        friendRequestsViewModel.friendRequests.observe(viewLifecycleOwner) { friendRequests ->
            /** Actualiza el RecyclerView con la lista de solicitudes obtenida del ViewModel.
             * Si la lista está vacía, se muestra un mensaje de "Actualmente no hay amistades".
             * Si la lista contiene amigos, se actualiza el RecyclerView con los datos.
             */
            if (friendRequests.isNotEmpty()) {
                adapter.submitList(friendRequests)
            } else {
                binding.friendRequestsRecyclerView.visibility = View.GONE
                binding.tvFriendRequestsEmpty.visibility = View.VISIBLE
            }
        }

        /*friendshipViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                // Handle error message (e.g., show a Toast or Snackbar)
            }
        }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}