package tfg.carlos.wereaderapp.ui.library.fragments.book

import android.app.Application
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import edu.carlosrivero.demo5.utils.checkConnection
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.local.datasource.LibraryLocalDataSource
import tfg.carlos.wereaderapp.data.remote.datasource.LibraryRemoteDadaSource
import tfg.carlos.wereaderapp.data.repository.LibraryRepository
import tfg.carlos.wereaderapp.databinding.FragmentBooksBinding


class BooksFragment : Fragment() {
    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!
    private var clickedItemPosition: Int = RecyclerView.NO_POSITION

    companion object {
        var needsRefresh = false
        fun newInstance() = BooksFragment()
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
                "Leyendo: $idBook",
                Toast.LENGTH_SHORT
            ).show()
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        binding.booksRecyclerView.adapter = adapter

        // Se carga la lista de libros de ROOM
        getBooks()

        // Funcionalidad del SwipeRefreshLayout
        /*if(checkConnection(requireActivity().application)) {
            binding.swipeRefreshLayout.setOnRefreshListener {
                vm.refreshBooks()
            }

            // Observa isLoading para mostrar u ocultar el indicador de carga
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    vm.isLoading.collect {
                        binding.swipeRefreshLayout.isRefreshing = it
                    }
                }
            }
        } else {
            binding.swipeRefreshLayout.isEnabled = false
            // Si no hay conexión, muestra un mensaje
            Toast.makeText(
                requireContext(),
                "No hay conexión, podrás leer los libros descargados",
                Toast.LENGTH_SHORT
            ).show()
        }*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getBooks() {
        adapter.submitList(null)

        lifecycleScope.launch {
            vm.books.collect { booksList ->
                Log.d("BooksFragment", "Libros recibidos: ${booksList.size}")
                adapter.submitList(booksList)
            }
        }
    }
}