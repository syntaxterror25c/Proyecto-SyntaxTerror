package com.example.aplicacion.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aplicacion.databinding.FragmentTabListRecursosBinding
import com.example.aplicacion.viewpager.TabRecursosPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.example.aplicacion.R

class TabListRecursosFragment : Fragment() {

    private var _binding: FragmentTabListRecursosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTabListRecursosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pagerAdapter = TabRecursosPagerAdapter(requireActivity())
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) getString(R.string.tab_todos) else getString(R.string.tab_favoritos)
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}