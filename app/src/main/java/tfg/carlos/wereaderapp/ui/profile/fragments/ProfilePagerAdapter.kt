package tfg.carlos.wereaderapp.ui.profile.fragments

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import tfg.carlos.wereaderapp.ui.profile.fragments.addfriend.AddFriendFragment
import tfg.carlos.wereaderapp.ui.profile.fragments.friendrequests.FriendRequestFragment
import tfg.carlos.wereaderapp.ui.profile.fragments.friends.FriendsFragment

class ProfilePagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int) = when (position) {
        0 -> FriendsFragment()
        1 -> FriendRequestFragment()
        2 -> AddFriendFragment()
        else -> throw IllegalStateException()
    }
}