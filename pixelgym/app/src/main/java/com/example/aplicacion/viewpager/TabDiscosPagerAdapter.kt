package com.example.aplicacion.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.aplicacion.fragments.ListSalasFavFragment
import com.example.aplicacion.fragments.ListSalasFragment

// Extiende FragmentStateAdapter para manejar Fragmentos dentro del ViewPager.

class TabDiscosPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    // Lista de Fragmentos
    private val fragments = listOf(
        ListSalasFragment(),      // Pestaña 0: Todos los discos
        ListSalasFavFragment()   // Pestaña 1: Solo favoritos
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}