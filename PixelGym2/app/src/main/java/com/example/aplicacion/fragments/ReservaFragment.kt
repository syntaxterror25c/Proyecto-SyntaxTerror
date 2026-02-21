package com.example.aplicacion.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.aplicacion.R
import com.example.aplicacion.databinding.FragmentReservaBinding
import com.example.aplicacion.viewmodels.GymViewModel
import com.example.aplicacion.viewmodels.GymViewModelFactory
import com.example.aplicacion.firebase.ServiceLocator
import kotlinx.coroutines.launch
import java.util.*
import java.text.SimpleDateFormat

class ReservaFragment : Fragment() {

    private var _binding: FragmentReservaBinding? = null
    private val binding get() = _binding!!

    private val gymViewModel: GymViewModel by activityViewModels {
        GymViewModelFactory(ServiceLocator.gymRepository, ServiceLocator.authRepository)
    }

    private var actividadId: String = ""
    private var nombreActividad: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReservaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actividadId = arguments?.getString("actividadId") ?: ""
        nombreActividad = arguments?.getString("nombreActividad") ?: "Actividad"

        binding.tvNombreRecursoReserva.text = nombreActividad

        binding.btnDetalles.setOnClickListener {
            val bundle = Bundle().apply { putString("actividadId", actividadId) }
            findNavController().navigate(R.id.action_reservaFragment_to_detalleActividadFragment, bundle)
        }

        binding.etFechaReserva.setOnClickListener { showDatePickerDialog() }

        binding.btnConfirmarReserva.setOnClickListener { confirmarReservaFinal() }

        observeSesiones()
    }

    private fun observeSesiones() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                gymViewModel.listaSesiones.collect { sesiones ->

                    // FILTRO DINÁMICO: Solo sesiones con plazas libres
                    val sesionesDisponibles = sesiones.filter { it.plazas_ocupadas < it.capacidad_maxima }

                    if (sesionesDisponibles.isEmpty()) {
                        val aviso = arrayOf("No hay disponibilidad este día")
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, aviso)
                        binding.spinnerHoras.setAdapter(adapter)
                        binding.spinnerHoras.setText("", false)
                        binding.btnConfirmarReserva.isEnabled = false
                    } else {
                        // Solo mapeamos las horas de las que tienen hueco
                        val horas = sesionesDisponibles.map { it.hora_inicio }
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, horas)
                        binding.spinnerHoras.setAdapter(adapter)

                        // Limpiamos el texto para que el usuario tenga que elegir
                        binding.spinnerHoras.setText("Selecciona hora", false)
                        binding.btnConfirmarReserva.isEnabled = true
                    }
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        val dpd = DatePickerDialog(requireContext(), { _, y, m, d ->
            val calendar = Calendar.getInstance().apply { set(y, m, d) }

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaFormateada = sdf.format(calendar.time)

            binding.etFechaReserva.setText(fechaFormateada)

            // LLAMADA A LA NUEVA FUNCIÓN DEL VIEWMODEL
            gymViewModel.cargarSesionesPorFecha(nombreActividad, fechaFormateada)

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        dpd.show()
    }

    private fun confirmarReservaFinal() {
        val horaSeleccionada = binding.spinnerHoras.text.toString()
        val fechaSeleccionada = binding.etFechaReserva.text.toString()

        if (horaSeleccionada.isNotEmpty() &&
            horaSeleccionada != "No hay disponibilidad este día" &&
            horaSeleccionada != "Selecciona hora") {

            // 1. Buscamos la sesión que quiere reservar
            val sesionParaReservar = gymViewModel.listaSesiones.value.find { it.hora_inicio == horaSeleccionada }

            sesionParaReservar?.let { sesion ->

                // 2. COMPROBACIÓN: ¿Ya tiene una reserva para esta FECHA y HORA?
                // Usamos 'listaMisReservas' y los campos 'fecha_sesion' y 'hora_inicio' de tu modelo Reserva
                val yaTieneReserva = gymViewModel.listaMisReservas.value.any { reserva ->
                    reserva.fecha_sesion == fechaSeleccionada && reserva.hora_inicio == horaSeleccionada
                }

                if (yaTieneReserva) {
                    // AVISO CON SNACKBAR EN NARANJA
                    com.google.android.material.snackbar.Snackbar.make(
                        binding.root,
                        "Atención: Ya tienes una clase reservada el $fechaSeleccionada a las $horaSeleccionada",
                        com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                    ).setBackgroundTint(resources.getColor(android.R.color.holo_orange_dark, null))
                        .show()
                } else {
                    // Si no hay conflicto, procedemos a reservar
                    gymViewModel.intentarReserva(sesion)
                    findNavController().popBackStack()
                }
            }
        } else {
            com.google.android.material.snackbar.Snackbar.make(
                binding.root,
                "Por favor, selecciona una hora válida",
                com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}