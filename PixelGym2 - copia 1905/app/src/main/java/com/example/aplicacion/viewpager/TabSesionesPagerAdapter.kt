package com.example.aplicacion.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.aplicacion.fragments.ListSesionesFragment
import com.example.aplicacion.fragments.ListReservasFragment

class TabSesionesPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    // Tenemos exactamente 2 pestañas: Cartelera y Mis Reservas
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ListSesionesFragment() // La primera pestaña (Cartelera)
            1 -> ListReservasFragment() // La segunda pestaña (Mis Reservas)
            else -> ListSesionesFragment()
        }
    }
}