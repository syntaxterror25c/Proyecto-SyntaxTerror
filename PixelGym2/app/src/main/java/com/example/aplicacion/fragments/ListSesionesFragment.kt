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
import com.example.aplicacion.databinding.FragmentListDiscosBinding
import com.example.aplicacion.recycler.ActividadAdapter // <--- Nuevo
import com.example.aplicacion.recycler.SesionAdapter
import com.example.aplicacion.viewmodels.GymViewModel
import com.example.aplicacion.viewmodels.GymViewModelFactory
import com.example.aplicacion.firebase.ServiceLocator
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController

class ListSesionesFragment : Fragment(com.example.aplicacion.R.layout.fragment_list_discos) {

    private var _binding: FragmentListDiscosBinding? = null
    private val binding get() = _binding!!

    private val gymViewModel: GymViewModel by activityViewModels {
        GymViewModelFactory(ServiceLocator.gymRepository, ServiceLocator.authRepository)
    }

    // Tenemoms los dos Adapters preparados
    private lateinit var sesionAdapter: SesionAdapter
    private lateinit var actividadAdapter: ActividadAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentListDiscosBinding.bind(view)

        // Mantenemos tus scripts de carga por si los necesitas
        viewLifecycleOwner.lifecycleScope.launch {
            // gymViewModel.crearDatosPrueba()
        }

        // 1. Cargamos el catálogo inicial (Actividades)
        gymViewModel.cargarActividades()

        setupRecyclerViews()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        binding.recyclerViewDiscos.layoutManager = LinearLayoutManager(context)

        // El de sesiones lo dejamos listo para cuando el usuario elija una actividad
        sesionAdapter = SesionAdapter(mutableListOf(), { sesion ->
            // AQUÍ SÍ NAVEGAMOS: Cuando ya tenemos una sesión con HORA
            val bundle = Bundle().apply {
                putString("idSesion", sesion.id)
                putString("nombreActividad", sesion.nombre_actividad)
                putString("hora", sesion.hora_inicio)
            }
            findNavController().navigate(com.example.aplicacion.R.id.action_tabSesiones_to_reservaFragment, bundle)
        }, requireContext())
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Observamos las ACTIVIDADES (Catálogo)
                launch {
                    gymViewModel.listaActividades.collect { actividades ->
                        actividadAdapter = ActividadAdapter(actividades, requireContext()) { actividadPinchada ->
                            // Al pulsar una actividad, pedimos sus sesiones al ViewModel
                            gymViewModel.cargarSesionesDeActividad(actividadPinchada.nombre)
                            binding.recyclerViewDiscos.adapter = sesionAdapter // Cambiamos el adapter
                        }
                        binding.recyclerViewDiscos.adapter = actividadAdapter
                    }
                }

                // Observamos las SESIONES (Horarios filtrados)
                launch {
                    gymViewModel.listaSesiones.collect { sesiones ->
                        sesionAdapter.updateData(sesiones)
                    }
                }

                // Tu lógica de mensajes de éxito original
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