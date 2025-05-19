package tfg.carlos.wereaderapp.ui.library.fragments.sharedlibrary

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.entity.BookEntity
import tfg.carlos.wereaderapp.data.local.datasource.LibraryLocalDataSource
import tfg.carlos.wereaderapp.data.remote.datasource.LibraryRemoteDadaSource
import tfg.carlos.wereaderapp.data.repository.LibraryRepository
import tfg.carlos.wereaderapp.databinding.FragmentSharedLibraryBinding
import tfg.carlos.wereaderapp.ui.library.fragments.library.BooksAdapter
import tfg.carlos.wereaderapp.ui.library.fragments.library.BooksViewModel
import tfg.carlos.wereaderapp.ui.library.fragments.library.BooksViewModelFactory
import tfg.carlos.wereaderapp.ui.reader.ReaderActivity
import tfg.carlos.wereaderapp.utils.BookMenuHandler

class SharedLibraryFragment : Fragment() {
    private var _binding: FragmentSharedLibraryBinding? = null
    private val binding get() = _binding!!
    private var clickedItemPosition: Int = RecyclerView.NO_POSITION

    companion object {
        fun newInstance() = SharedLibraryFragment()
    }

    private val vm: BooksViewModel by viewModels {
        val db = (requireActivity().application as WeReaderApplication).weReaderDB
        val localDataSource = LibraryLocalDataSource(db.bookDao())
        val remoteDadaSource = LibraryRemoteDadaSource()
        val repository = LibraryRepository(remoteDadaSource, localDataSource)
        BooksViewModelFactory(repository)
    }

    private val adapter = BooksAdapter (
        onClickBookItem = { book: BookEntity, position: Int ->
            clickedItemPosition = position
            vm.updateBookReadingStatus(book.id, true)
            // Se ejecuta la lectura del libro con Readium
            lifecycleScope.launch {
                openReaderActivity(book.id)
            }
        },
        onLongClickBookItem = { idBook: String, position: Int, isPending: Boolean ->
            clickedItemPosition = position
            showBookOptionsMenu(binding.booksRecyclerView, idBook, isPending, position)
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
        _binding = FragmentSharedLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getBooks() {
        adapter.submitList(null)

        lifecycleScope.launch {
            vm.sharedBooks.collect { booksList ->
                Log.d("BooksFragment", "Libros recibidos: ${booksList.size}")
                adapter.submitList(booksList)
            }
        }
    }

    private suspend fun openReaderActivity(idBook: String) {
        vm.getBookById(idBook).let { book ->
            val epubPath = book.epubUrl
            val intent = Intent(requireContext(), ReaderActivity::class.java)
            intent.putExtra("bookPath", epubPath)
            intent.putExtra("bookId", book.id)
            startActivity(intent)
        }
    }

    // Mostrar el menú de opciones del libro
    private fun showBookOptionsMenu(
        recyclerView: RecyclerView,
        idBook: String,
        isPending: Boolean,
        position: Int
    ) {
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position) ?: return
        val anchorView = viewHolder.itemView

        BookMenuHandler.show(
            context = requireContext(),
            anchorView = anchorView,
            isPending = isPending,
            onRead = {
                lifecycleScope.launch {
                    openReaderActivity(idBook)
                }
            },
            onDetail = {
                // TODO: abrir un detalle del libro
                // startActivity(Intent(this, BookDetailActivity::class.java))
            },
            updatePending = { pending ->
                vm.updateBookPendingStatus(idBook, pending)
            },
            updateReading = { reading ->
                vm.updateBookReadingStatus(idBook, reading)
            },
            updateMarkReadOrUnreadBook = { progress ->
                vm.updateMarkReadOrUnreadBook(idBook, progress)
            },
        )
    }
}