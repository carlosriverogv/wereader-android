package tfg.carlos.wereaderapp.ui.library.fragments

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tfg.carlos.wereaderapp.R

class CollectionsFragment : Fragment() {

    companion object {
        fun newInstance() = CollectionsFragment()
    }

    private val viewModel: CollectionsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_collections, container, false)
    }
}