package tfg.carlos.wereaderapp.ui.library.fragments.library

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.local.datasource.LibraryLocalDataSource
import tfg.carlos.wereaderapp.data.remote.datasource.LibraryRemoteDadaSource
import tfg.carlos.wereaderapp.data.repository.LibraryRepository
import tfg.carlos.wereaderapp.databinding.FragmentLibraryBinding


class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    private var clickedItemPosition: Int = RecyclerView.NO_POSITION

    companion object {
        fun newInstance() = LibraryFragment()
    }

    private val vm: BooksViewModel by viewModels {
        val db = (requireActivity().application as WeReaderApplication).weReaderDB
        val localDataSource = LibraryLocalDataSource(db.bookDao())
        val remoteDadaSource = LibraryRemoteDadaSource()
        val repository = LibraryRepository(remoteDadaSource, localDataSource)
        BooksViewModelFactory(repository)
    }

    private val adapter = BooksAdapter (
        onClickBookItem = { idBook: String, position: Int ->
            clickedItemPosition = position
            // TODO: Se ejecuta la lectura del libro con FileReader
            vm.updateBookReadingStatus(idBook, true)
            Toast.makeText(
                requireContext(),
                "Abriendo el libro",
                Toast.LENGTH_SHORT
            ).show()
            // TODO: Se ejecuta la lectura del libro con FileReader
        },
        onLongClickBookItem = { idBook: String, position: Int, isPending: Boolean ->
            clickedItemPosition = position
            showBookOptionsMenu(idBook, isPending)
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        binding.booksRecyclerView.adapter = adapter

        // Se carga la lista de libros de ROOM
        getBooks()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getBooks() {
        adapter.submitList(null)

        lifecycleScope.launch {
            vm.myBooks.collect { booksList ->
                adapter.submitList(booksList)
            }
        }
    }

    private fun showBookOptionsMenu(idBook: String, isPending: Boolean) {
        val viewHolder = binding.booksRecyclerView.findViewHolderForAdapterPosition(clickedItemPosition)
            ?: return

        val anchorView = viewHolder.itemView

        val popupMenu = android.widget.PopupMenu(requireContext(), anchorView)
        popupMenu.menuInflater.inflate(R.menu.book_options_menu, popupMenu.menu)

        val togglePendingTitle =
            if (isPending) getString(R.string.library_menu_remove_pending)
            else getString(R.string.library_menu_add_pending)
        popupMenu.menu.findItem(R.id.action_toggle_pending).title = togglePendingTitle

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_read -> {
                    vm.updateBookReadingStatus(idBook, true)
                    // TODO: Se ejecuta la lectura del libro con FileReader
                    Toast.makeText(
                        requireContext(),
                        "Abriendo el libro",
                        Toast.LENGTH_SHORT,
                    ).show()
                    true
                }
                R.id.action_toggle_pending -> {
                    vm.updateBookPendingStatus(idBook, !isPending)
                    Toast.makeText(
                        requireContext(),
                        if (!isPending) getString(R.string.library_menu_add_pending_response)
                        else getString(R.string.library_menu_remove_pending_response),
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }
                R.id.action_toggle_read -> {
                    vm.updateBookReadingStatus(idBook, false)
                    // TODO: Poner progreso de lectura a 100%
                    Toast.makeText(requireContext(), getString(R.string.library_menu_mark_read), Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_detail -> {
                    // TODO: Ir a la vista de detalle del libro
                    Toast.makeText(requireContext(), "Detalle del libro", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
}