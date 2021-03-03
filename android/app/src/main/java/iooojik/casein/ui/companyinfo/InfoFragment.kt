package iooojik.casein.ui.companyinfo

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import iooojik.casein.R
import iooojik.casein.StaticVars
import iooojik.casein.localData.AppDatabase

class InfoFragment : Fragment() {

    private lateinit var rootView : View
    private lateinit var database: AppDatabase
    private lateinit var preferences: SharedPreferences
    private lateinit var viewPager : ViewPager
    private lateinit var tabLayout : TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_info, container, false)
        initialization()
        return rootView
    }

    private fun initialization(){
        preferences = requireActivity().getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)
        database = AppDatabase.getAppDataBase(requireContext())!!

        viewPager = rootView.findViewById(R.id.view_pager)
        tabLayout = rootView.findViewById(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)

        val viewPagerAdapter = ViewPagerAdapter(childFragmentManager, 0)
        for (i in 0 until 7){
            viewPagerAdapter.addFragment(InfoPart(i), i.toString())
        }
        viewPager.adapter = viewPagerAdapter
        viewPager.currentItem = preferences.getInt("0", 0)
    }

    private class ViewPagerAdapter(fm: FragmentManager, behavior: Int) :
        FragmentPagerAdapter(fm, behavior) {

        val fragments = mutableListOf<Fragment>()
        val fragmentsTitles = mutableListOf<String>()


        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment : Fragment, title : String){
            fragments.add(fragment)
            fragmentsTitles.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getPageTitle(position: Int) : CharSequence{
            return fragmentsTitles[position]
        }
    }


}