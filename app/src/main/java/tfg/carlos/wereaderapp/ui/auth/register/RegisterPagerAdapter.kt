package tfg.carlos.wereaderapp.ui.auth.register

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import tfg.carlos.wereaderapp.ui.auth.register.fragments.RegisterStep1Fragment
import tfg.carlos.wereaderapp.ui.auth.register.fragments.RegisterStep2Fragment
import tfg.carlos.wereaderapp.ui.auth.register.fragments.RegisterStep3Fragment

class RegisterPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> RegisterStep1Fragment()
        1 -> RegisterStep2Fragment()
        2 -> RegisterStep3Fragment()
        else -> throw IllegalStateException("Paso no válido")
    }
}