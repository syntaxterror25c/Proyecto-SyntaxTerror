package com.example.aplicacion.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicacion.databinding.FragmentListDiscosBinding
import com.example.aplicacion.recycler.SesionAdapter
import com.example.aplicacion.viewmodels.GymViewModel
import com.example.aplicacion.viewmodels.GymViewModelFactory
import com.example.aplicacion.firebase.ServiceLocator
import kotlinx.coroutines.launch

class ListReservasFragment : Fragment(com.example.aplicacion.R.layout.fragment_list_discos) {

    private var _binding: FragmentListDiscosBinding? = null
    private val binding get() = _binding!!

    private val gymViewModel: GymViewModel by activityViewModels {
        GymViewModelFactory(ServiceLocator.gymRepository, ServiceLocator.authRepository)
    }

    private lateinit var sesionAdapter: SesionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentListDiscosBinding.bind(view)

        // Cargamos las reservas privadas del usuario
        gymViewModel.cargarMisReservas()

        sesionAdapter = SesionAdapter(
            mutableListOf(),
            { /* Acción al pulsar en reserva (ej: ver detalle o cancelar) */ },
            requireContext()
        )

        binding.recyclerViewDiscos.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = sesionAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                gymViewModel.listaMisReservas.collect { reservas ->
                    // Convertimos objetos 'Reserva' a 'Sesion' para que el adapter los pinte
                    val listaComoSesiones = reservas.map { it.toSesion() }
                    sesionAdapter.updateData(listaComoSesiones)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}