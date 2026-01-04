package es.cifpcarlos3.viewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import es.cifpcarlos3.fragments.FavFragment
import es.cifpcarlos3.fragments.ListFragment

// Adaptador del ViewPager2.
// Devuelve el fragment correcto según la pestaña (posición).
class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    // Solo tengo dos pestañas: Lista y Favoritos.
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ListFragment()
            1 -> FavFragment()
            else -> ListFragment()
        }
    }
}
