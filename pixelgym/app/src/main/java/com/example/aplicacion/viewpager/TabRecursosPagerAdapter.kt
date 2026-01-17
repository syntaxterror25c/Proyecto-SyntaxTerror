package com.example.aplicacion.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.aplicacion.fragments.ListRecursosFragment
import com.example.aplicacion.fragments.ListRecursosFavFragment

/**
 * Este adaptador es el que gestiona las pestañas del ViewPager2.
 * Recibe una FragmentActivity, por eso 'requireActivity()' funciona aquí.
 */
class TabRecursosPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ListRecursosFragment()
            1 -> ListRecursosFavFragment()
            else -> ListRecursosFragment()
        }
    }
}