package tfg.carlos.wereaderapp.ui.discover.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import tfg.carlos.wereaderapp.R
import tfg.carlos.wereaderapp.data.remote.datasource.BookRemoteDataSource
import tfg.carlos.wereaderapp.data.remote.datasource.FriendshipRemoteDataSource
import tfg.carlos.wereaderapp.data.remote.datasource.UserRemoteDataSource
import tfg.carlos.wereaderapp.data.repository.BookRepository
import tfg.carlos.wereaderapp.data.repository.FriendshipRepository
import tfg.carlos.wereaderapp.data.repository.UserRepository
import tfg.carlos.wereaderapp.databinding.FragmentBookSearchBinding
import tfg.carlos.wereaderapp.ui.bookDetail.BookDetailActivity
import tfg.carlos.wereaderapp.ui.profile.fragments.addfriend.AddFriendViewModel
import tfg.carlos.wereaderapp.ui.profile.fragments.addfriend.AddFriendViewModelFactory

class BookSearchFragment : Fragment() {
    private var _binding: FragmentBookSearchBinding? = null
    private val binding get() = _binding!!
    private var clickedItemPosition: Int = RecyclerView.NO_POSITION

    // ViewModel
    private val bookSearchViewModel: BookSearchViewModel by viewModels {
        val bookRemoteDataSource = BookRemoteDataSource()
        val bookRepository = BookRepository(bookRemoteDataSource)

        BookSearchViewModelFactory(bookRepository)
    }

    // Adapter
    private val bookSearchAdapter = BookSearchAdapter(
        onClickBookItem = { book, position ->
            clickedItemPosition = position
            openBookDetailActivity(book.id)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBookSearchBinding.inflate(inflater, container, false)

        // Setup toolbar
        setupToolbar()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView and Adapter
        binding.searchResultsRecyclerView.adapter = bookSearchAdapter

        val divider = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        binding.searchResultsRecyclerView.addItemDecoration(divider)

        // Setup search functionality
        loadSearchResults()

        binding.searchEditText.doAfterTextChanged { editable ->
            val text = editable?.toString()?.trim() ?: ""

            if (text.isEmpty()) {
                // Limpiar resultados si el campo está vacío
                bookSearchAdapter.submitList(emptyList())
            } else {
                // Ejecutar búsqueda normalmente
                bookSearchViewModel.updateSearchQuery(text)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun loadSearchResults() {
        bookSearchAdapter.submitList(null)

        // Se observa la lista de resultados de búsqueda del ViewModel
        bookSearchViewModel.searchResults.observe(viewLifecycleOwner) { bookList ->
            bookSearchAdapter.submitList(bookList)
            Log.d("BookSearchFragment", "Resultados de búsqueda actualizados: ${bookList.size} libros encontrados")
        }
    }

    private fun openBookDetailActivity(bookId: String) {
        // Implementar la lógica para abrir la actividad de detalle del libro

        val intent = Intent(context, BookDetailActivity::class.java).apply {
             putExtra(BookDetailActivity.EXTRA_BOOK_ID, bookId)
             putExtra(BookDetailActivity.EXTRA_IS_STORE_BOOK, true)
         }
        startActivity(intent)
    }

    private fun setupToolbar() {
        binding.toolbarBookSearch.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

}