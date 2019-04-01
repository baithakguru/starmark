package guru.baithak.starmark.ui.mainScreen

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity


class FragmentPager(c:Context ,fm : FragmentManager, frags : ArrayList<Fragment>,names:ArrayList<String>  ) : FragmentPagerAdapter(fm) {
    var c:Context = c
    var frags:ArrayList<Fragment> = frags
    var names:ArrayList<String> = names

    override fun getItem(p0: Int): Fragment {
        return frags[p0]
    }

    override fun getCount(): Int {
        return frags.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return names[position]
    }
}