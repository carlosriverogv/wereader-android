package tfg.carlos.wereaderapp.ui.profile.fragments.addfriend

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.data.model.user.User
import tfg.carlos.wereaderapp.data.remote.datasource.FriendshipRemoteDataSource
import tfg.carlos.wereaderapp.data.remote.datasource.UserRemoteDataSource
import tfg.carlos.wereaderapp.data.repository.FriendshipRepository
import tfg.carlos.wereaderapp.data.repository.UserRepository
import tfg.carlos.wereaderapp.databinding.FragmentAddFriendBinding
import tfg.carlos.wereaderapp.ui.profile.fragments.FriendshipAdapter
import tfg.carlos.wereaderapp.ui.profile.fragments.FriendshipAdapterMode


class AddFriendFragment : Fragment() {
    private var _binding: FragmentAddFriendBinding? = null
    private val binding get() = _binding!!
    private var clickedItemPosition: Int = RecyclerView.NO_POSITION

    private val addFriendViewModel: AddFriendViewModel by viewModels {
        val userRemoteDataSource = UserRemoteDataSource()
        val userRepository = UserRepository(
            userRemoteDataSource)

        val friendshipRemoteDadaSource = FriendshipRemoteDataSource()
        val friendshipRepository = FriendshipRepository(
            friendshipRemoteDadaSource)

        AddFriendViewModelFactory(userRepository, friendshipRepository)
    }

    private val adapter = FriendshipAdapter(
        mode = FriendshipAdapterMode.SEARCH,
        onClickFriendOptionsButton = { friend: User, position: Int ->
            clickedItemPosition = position
            sendRequestFriend(friend)
            hideKeyboard()
        }
        //, onLongClickBookItem = { book, position -> /* TODO: Implement long click action */ }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddFriendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cargar el adapter
        binding.searchResultsRecyclerView.adapter = adapter

        loadSearchResults()

        // Usar doAfterTextChanged para actualizar la búsqueda en tiempo real
        // Esta función se ejecuta cada vez que el texto cambia
        binding.searchEditText.doAfterTextChanged { editable ->
            val text = editable?.toString()?.trim() ?: ""

            if (text.isEmpty()) {
                // Limpiar resultados si el campo está vacío
                adapter.submitList(emptyList())
            } else {
                // Ejecutar búsqueda normalmente
                addFriendViewModel.updateSearchQuery(text)
            }
        }

        showError()

        // Forma alternativa para el buscador en tiempo real
        /*binding.searchEditText.addTextChangedListener(object : TextWatcher {
            private var searchJob: Job? = null

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(200) // debounce de 400ms
                    val tag = s?.toString()?.trim()
                    if (!tag.isNullOrEmpty()) {
                        addFriendViewModel.searchUserByTag(tag)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Método para cargar los resultados de búsqueda de amigos.
     * Observa los resultados del ViewModel y actualiza el RecyclerView.
     */
    private fun loadSearchResults() {
        adapter.submitList(null)

        addFriendViewModel.searchResults.observe(viewLifecycleOwner) { userList ->
            adapter.submitList(userList) // actualiza el RecyclerView
        }
    }

    /**
     * Método para enviar una solicitud de amistad.
     * Muestra un diálogo de confirmación antes de enviar la solicitud.
     * @param friend El usuario al que se le enviará la solicitud de amistad.
     */
    private fun sendRequestFriend(friend: User) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.alert_dialog_add_friend_title))
            .setMessage(getString(R.string.alert_dialog_add_friend_message, friend.tag))
            .setPositiveButton(getString(R.string.alert_dialog_add_friend_positive)) { _, _ ->
                // Se comparte la biblioteca con el amigo
                addFriendViewModel.createFriendship(friend.id)
            }
            .setNegativeButton(getString(R.string.alert_dialog_add_friend_negative), null)
            .show()
    }

    /**
     * Método para mostrar un mensaje de error utilizando Snackbar.
     * Observa el mensaje de error del ViewModel y lo muestra si está presente.
     */
    private fun showError() {
        addFriendViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
                    .setAnchorView(R.id.bottom_navigation)
                    .show()
                // Limpiar el mensaje después de mostrarlo
                addFriendViewModel.clearErrorMessage()
            }
        }
    }

    /**
     * Método para ocultar el teclado.
     * Se utiliza cuando se envía una solicitud de amistad para evitar que el teclado
     * interfiera con la interfaz de usuario.
     */
    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE)
                as android.view.inputmethod.InputMethodManager
        val view = requireActivity().currentFocus ?: View(requireContext())
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}