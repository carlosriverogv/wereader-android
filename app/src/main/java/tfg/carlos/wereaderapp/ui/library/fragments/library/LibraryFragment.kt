package tfg.carlos.wereaderapp.ui.library.fragments.library

import android.content.Intent
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
import tfg.carlos.wereaderapp.data.entity.BookEntity
import tfg.carlos.wereaderapp.data.local.datasource.LibraryLocalDataSource
import tfg.carlos.wereaderapp.data.remote.datasource.LibraryRemoteDadaSource
import tfg.carlos.wereaderapp.data.repository.LibraryRepository
import tfg.carlos.wereaderapp.databinding.FragmentLibraryBinding
import tfg.carlos.wereaderapp.ui.reader.ReaderActivity
import tfg.carlos.wereaderapp.utils.BookMenuHandler


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
        onClickBookItem = { book: BookEntity, position: Int ->
            clickedItemPosition = position
            vm.updateBookReadingStatus(book.id, true)

            // TODO: Se ejecuta la lectura del libro con Readium
            val epubPath = book.epubUrl
            val intent = Intent(requireContext(), ReaderActivity::class.java)
            intent.putExtra("bookPath", epubPath)
            intent.putExtra("bookId", book.id)
            startActivity(intent)
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
            idBook = idBook,
            isPending = isPending,
            updateReading = { reading ->
                vm.updateBookReadingStatus(idBook, reading)
            },
            updatePending = { pending ->
                vm.updateBookPendingStatus(idBook, pending)
            },
            onRead = {
                // TODO: Se ejecuta la lectura del libro con FileReader
                Toast.makeText(requireContext(), "Abriendo el libro", Toast.LENGTH_SHORT).show()
            },
            onDetail = {
                // TODO: abrir un detalle del libro
                // startActivity(Intent(this, BookDetailActivity::class.java))
            }
        )
    }
}