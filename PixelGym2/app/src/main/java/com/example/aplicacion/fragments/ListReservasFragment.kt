package com.example.aplicacion.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicacion.databinding.FragmentListReservasBinding
// Eliminado el SesionAdapter ya que usamos ReservaAdapter
import com.example.aplicacion.viewmodels.GymViewModel
import com.example.aplicacion.viewmodels.GymViewModelFactory
import com.example.aplicacion.firebase.ServiceLocator
import kotlinx.coroutines.launch
import com.example.aplicacion.recycler.ReservaAdapter
import com.example.aplicacion.model.Reserva

class ListReservasFragment : Fragment(com.example.aplicacion.R.layout.fragment_list_reservas) {

    private var _binding: FragmentListReservasBinding? = null
    private val binding get() = _binding!!

    private val gymViewModel: GymViewModel by activityViewModels {
        GymViewModelFactory(ServiceLocator.gymRepository, ServiceLocator.authRepository)
    }

    private lateinit var reservaAdapter: ReservaAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentListReservasBinding.bind(view)

        gymViewModel.cargarMisReservas()

        // 1. SOLUCIÓN AL ERROR: Especificamos el tipo (reserva: Reserva)
        reservaAdapter = ReservaAdapter(emptyList()) { reserva: Reserva ->
        }

        binding.recyclerViewReservas.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reservaAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                gymViewModel.listaMisReservas.collect { reservas ->
                    // 2. Aquí también especificamos el tipo para evitar confusiones
                    reservaAdapter = ReservaAdapter(reservas) { reservaAnular: Reserva ->
                        // Acción de anular
                        gymViewModel.anularReserva(reservaAnular)
                    }
                    binding.recyclerViewReservas.adapter = reservaAdapter
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}