package com.example.aplicacion.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicacion.databinding.FragmentMisReservasBinding
import com.example.aplicacion.recycler.ReservasAdapter
import com.example.aplicacion.viewmodels.RecursosViewModel

class MisReservasFragment : Fragment() {

    private var _binding: FragmentMisReservasBinding? = null
    private val binding get() = _binding!!
    private val recursosViewModel: RecursosViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMisReservasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ReservasAdapter(emptyList())
        binding.rvMisReservas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMisReservas.adapter = adapter

        // Observamos las reservas reales del ViewModel
        recursosViewModel.misReservas.observe(viewLifecycleOwner) { lista ->
            adapter.updateList(lista)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}