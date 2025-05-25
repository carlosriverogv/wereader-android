package tfg.carlos.wereaderapp.ui.profile.fragments.friends

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.local.datasource.LibraryLocalDataSource
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

class FriendsFragment : Fragment() {
    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!
    private var clickedItemPosition: Int = RecyclerView.NO_POSITION

    private val friendshipViewModel: FriendsViewModel by viewModels {
        val friendshipRemoteDadaSource = FriendshipRemoteDataSource()
        val friendshipRepository = FriendshipRepository(friendshipRemoteDadaSource)
        FriendsViewModelFactory(friendshipRepository)
    }

    private val adapter = FriendsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        // TODO: Instanciar el adapter y asignarlo al RecyclerView
        binding.friendsRecyclerView.adapter = adapter

        // TODO: Cargar la lista de amigos desde el ViewModel
        getFriends()
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
        friendshipViewModel.friends.observe(viewLifecycleOwner) { friendList ->
            adapter.submitList(friendList)  // friendList ya es un List<UserFriendshipsResponseItem>
        }
    }
}