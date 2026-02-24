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

        // SOLUCIÓN A ERROR: Especificamos el tipo para cumplir promesa (lateinit var)
        reservaAdapter = ReservaAdapter(emptyList()) { reserva: Reserva ->        }

        binding.recyclerViewReservas.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reservaAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                gymViewModel.listaMisReservas.collect { reservas ->
                    val isEmpty = reservas.isEmpty()
                    binding.tvEmptyReservas.visibility = if (isEmpty) View.VISIBLE else View.GONE
                    binding.recyclerViewReservas.visibility = if (isEmpty) View.GONE else View.VISIBLE
                    reservaAdapter = ReservaAdapter(reservas) { reservaAnular: Reserva ->
                        // Ejecutamos la anulación en el ViewModel
                        gymViewModel.anularReserva(reservaAnular)

                        // Lanzamos el Snackbar ANCLADO A LA ACTIVITY
                        val rootView = requireActivity().findViewById<View>(android.R.id.content)
                        com.google.android.material.snackbar.Snackbar.make(
                            rootView,
                            "Reserva de ${reservaAnular.nombre_actividad} anulada",
                            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                        ).apply {
                            setBackgroundTint(resources.getColor(android.R.color.holo_orange_dark, null))
                            setAction("OK") { dismiss() }
                            show()
                        }
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