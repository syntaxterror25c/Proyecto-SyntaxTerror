package com.example.aplicacion.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aplicacion.databinding.FragmentTabListDiscosBinding // Puedes renombrar el XML a fragment_tab_sesiones si quieres
import com.example.aplicacion.viewpager.TabSesionesPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.example.aplicacion.R

class TabSesionesFragment : Fragment() {

    private var _binding: FragmentTabListDiscosBinding? = null
    private val binding get() = _binding!!

    private val tabTitles: Array<String>
        get() = arrayOf(
            "Cartelera",
            "Mis Reservas"
        )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabListDiscosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // El PagerAdapter también debe renombrarse para ser coherente
        val pagerAdapter = TabSesionesPagerAdapter(requireActivity())
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}