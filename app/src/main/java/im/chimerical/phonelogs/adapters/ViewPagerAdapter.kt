package im.chimerical.phonelogs.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import im.chimerical.phonelogs.fragments.CallLogsFragment
import im.chimerical.phonelogs.fragments.ContactsFragment

class ViewPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment = Fragment()
        if (position == 0)
            fragment =  CallLogsFragment()
        else if (position == 1)
            fragment = ContactsFragment()
        return fragment
    }
}