package com.example.aplicacion.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicacion.databinding.FragmentListReservasBinding
import com.example.aplicacion.recycler.ActividadAdapter
import com.example.aplicacion.viewmodels.GymViewModel
import com.example.aplicacion.viewmodels.GymViewModelFactory
import com.example.aplicacion.firebase.ServiceLocator
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController


class ListSesionesFragment : Fragment(com.example.aplicacion.R.layout.fragment_list_reservas) {

    private var _binding: FragmentListReservasBinding? = null
    private val binding get() = _binding!!

    private val gymViewModel: GymViewModel by activityViewModels {
        GymViewModelFactory(ServiceLocator.gymRepository, ServiceLocator.authRepository)
    }

    private lateinit var actividadAdapter: ActividadAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentListReservasBinding.bind(view)


        // 1. Cargamos el catálogo inicial (Actividades)
        gymViewModel.cargarActividades()

        setupRecyclerViews()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        binding.recyclerViewReservas.layoutManager = LinearLayoutManager(context)
        // Ya no necesitamos inicializar el sesionAdapter aquí,
        // porque las sesiones se verán en el Fragment de Reserva.
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Observamos las ACTIVIDADES (Catálogo)
                launch {
                    gymViewModel.listaActividades.collect { actividades ->
                        actividadAdapter = ActividadAdapter(actividades, requireContext()) { actividadPinchada ->

                            val bundle = Bundle().apply {
                                putString("actividadId", actividadPinchada.id)
                                putString("nombreActividad", actividadPinchada.nombre)
                            }

                            findNavController().navigate(
                                com.example.aplicacion.R.id.action_tabSesiones_to_reservaFragment,
                                bundle
                            )
                        }
                        binding.recyclerViewReservas.adapter = actividadAdapter
                    }
                }

                // Eliminamos la observación de listaSesiones aquí,
                // ya que ahora se hará en el ReservaFragment.

                launch {
                    gymViewModel.reservaStatus.collect { exito ->
                        exito?.let {
                            val msg = if (it) "¡Reserva confirmada!" else "Error en la reserva"
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                            gymViewModel.resetReservaStatus()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}