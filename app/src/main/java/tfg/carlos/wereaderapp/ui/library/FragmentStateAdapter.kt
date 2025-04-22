package tfg.carlos.wereaderapp.ui.library

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import tfg.carlos.wereaderapp.ui.library.fragments.BooksFragment
import tfg.carlos.wereaderapp.ui.library.fragments.CollectionsFragment
import tfg.carlos.wereaderapp.ui.library.fragments.SharedLibraryFragment

class LibraryPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BooksFragment()
            1 -> SharedLibraryFragment()
            2 -> CollectionsFragment()
            else -> throw IllegalStateException()
        }
    }
}