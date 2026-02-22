package com.example.aplicacion.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import com.example.aplicacion.utils.ImageMapper
import kotlinx.coroutines.launch
import java.util.*
import java.text.SimpleDateFormat
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast

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

        // Recuperar datos de los argumentos
        actividadId = arguments?.getString("actividadId") ?: ""
        nombreActividad = arguments?.getString("nombreActividad") ?: "Actividad"

        binding.tvNombreRecursoReserva.text = nombreActividad

        // --- PINTAR LA IMAGEN DE LA ACTIVIDAD ---
        val actividadData = gymViewModel.listaActividades.value.find { it.id == actividadId || it.nombre == nombreActividad }
        val resourceId = ImageMapper.getDrawableId(actividadData?.imagen)

        com.bumptech.glide.Glide.with(this)
            .load(resourceId)
            .centerCrop()
            .into(binding.imgReservaHeader)
        // -----------------------------------------------

        // 2. Listeners y Observadores (Tus funciones originales)
        binding.btnDetalles.setOnClickListener {
            val bundle = Bundle().apply { putString("actividadId", actividadId) }
            findNavController().navigate(R.id.action_reservaFragment_to_detalleActividadFragment, bundle)
        }

        binding.etFechaReserva.setOnClickListener { showDatePickerDialog() }

        binding.btnConfirmarReserva.setOnClickListener { confirmarReservaFinal() }

        // --- DETECTAR SELECCIÓN DE HORA PARA MOSTRAR PLAZAS ---
        binding.spinnerHoras.setOnItemClickListener { _, _, _, _ ->
            val horaSel = binding.spinnerHoras.text.toString()

            // Buscamos la sesión en el ViewModel
            val sesion = gymViewModel.listaSesiones.value.find { it.hora_inicio == horaSel }

            sesion?.let {
                val libres = it.capacidad_maxima - it.plazas_ocupadas
                binding.tvPlazasDisponibles.text = "Quedan $libres plazas de ${it.capacidad_maxima}"

                // Cambio de color dinámico
                if (libres <= 2) {
                    binding.tvPlazasDisponibles.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
                } else {
                    binding.tvPlazasDisponibles.setTextColor(resources.getColor(android.R.color.secondary_text_dark, null))
                }
            }
        }

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
            binding.tvPlazasDisponibles.text = "Selecciona una hora para ver disponibilidad"
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
                    añadirAlCalendario(nombreActividad, fechaSeleccionada, horaSeleccionada)
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

    private fun añadirAlCalendario(nombreAct: String, fecha: String, hora: String) {
        try {
            // 1. Convertimos el texto de fecha y hora a milisegundos
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val fechaCita = sdf.parse("$fecha $hora")
            val inicioMillis: Long = fechaCita?.time ?: System.currentTimeMillis()

            // 2. Creamos el Intent para el calendario
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, "Clase de $nombreAct")
                putExtra(CalendarContract.Events.DESCRIPTION, "Reserva realizada desde la App del Gimnasio")
                putExtra(CalendarContract.Events.EVENT_LOCATION, "Gimnasio Oloman")
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, inicioMillis)
                // Ponemos que dure 1 hora (3600000 milisegundos)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, inicioMillis + 3600000)
            }

            // 3. Lanzamos la actividad del calendario
            startActivity(intent)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "No se pudo abrir el calendario", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}