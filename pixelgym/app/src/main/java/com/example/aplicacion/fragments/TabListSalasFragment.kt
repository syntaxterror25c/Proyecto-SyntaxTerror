package com.example.aplicacion.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aplicacion.databinding.FragmentTabListSalasBinding
import com.example.aplicacion.viewpager.TabDiscosPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.example.aplicacion.R
class TabListSalasFragment : Fragment() {

    private var _binding: FragmentTabListSalasBinding? = null
    private val binding get() = _binding!!

    private val tabTitles: Array<String>
        get() = arrayOf(
            getString(R.string.tab_todos),
            getString(R.string.tab_favoritos)
        )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabListSalasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // incializar adaptador ViewPager
        // requireActivity() para el FragmentStateAdapter por pestañas estáticas
        val pagerAdapter = TabDiscosPagerAdapter(requireActivity())
        binding.viewPager.adapter = pagerAdapter

        // conectar TabLayout con el ViewPager2 usando TabLayoutMediator
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}