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
import com.example.aplicacion.utils.ImageMapper
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

        // Cargamos las reservas para la validación de duplicados
        gymViewModel.cargarMisReservas()

        actividadId = arguments?.getString("actividadId") ?: ""
        nombreActividad = arguments?.getString("nombreActividad") ?: "Actividad"
        binding.tvNombreRecursoReserva.text = nombreActividad

        // Carga de imagen con Glide
        val actividadData = gymViewModel.listaActividades.value.find { it.id == actividadId || it.nombre == nombreActividad }
        val resourceId = ImageMapper.getDrawableId(actividadData?.imagen)
        com.bumptech.glide.Glide.with(this).load(resourceId).centerCrop().into(binding.imgReservaHeader)

        // Listeners
        binding.btnDetalles.setOnClickListener {
            val bundle = Bundle().apply { putString("actividadId", actividadId) }
            findNavController().navigate(R.id.action_reservaFragment_to_detalleActividadFragment, bundle)
        }

        binding.btnConfirmarReserva.setOnClickListener { ejecutarProcesoReserva() }
        binding.etFechaReserva.setOnClickListener { showDatePickerDialog() }

        // Manejo del Spinner de horas
        binding.spinnerHoras.setOnItemClickListener { _, _, _, _ ->
            val horaSel = binding.spinnerHoras.text.toString()
            val sesion = gymViewModel.listaSesiones.value.find { it.hora_inicio == horaSel }
            sesion?.let {
                val libres = it.capacidad_maxima - it.plazas_ocupadas
                binding.tvPlazasDisponibles.text = "${getString(R.string.quedan)} $libres ${getString(R.string.plazas_de)} ${it.capacidad_maxima}"
                val colorRes = if (libres <= 2) {
                    R.color.naranja_fuego
                } else {
                    R.color.texto_secundario
                }
                val colorReal = androidx.core.content.ContextCompat.getColor(requireContext(), colorRes)
                binding.tvPlazasDisponibles.setTextColor(colorReal)
            }
        }

        observeReservaStatus()
        observeSesiones()
    }

    private fun observeReservaStatus() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                gymViewModel.reservaStatus.collect { exito ->
                    exito?.let { esExito ->
                        if (esExito) {
                            // Reseteamos el estado inmediatamente
                            gymViewModel.resetReservaStatus()

                            // Lanzamos el Snackbar ANCLADO A LA ACTIVITY (para que se vea al volver)
                            val rootView = requireActivity().findViewById<View>(android.R.id.content)
                            com.google.android.material.snackbar.Snackbar.make(
                                rootView, getString(R.string.reserva_ok), com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
                            ).apply {
                                setBackgroundTint(resources.getColor(android.R.color.holo_green_dark, null))
                                show()
                            }

                            // Volvemos atrás SIN ESPERAR
                            if (isAdded) findNavController().navigateUp()

                        } else {
                            // Si hay error, mostramos el mensaje aquí mismo y rehabilitamos botón
                            val msg = getString(R.string.reserva_error)
                            com.google.android.material.snackbar.Snackbar.make(binding.root, msg, com.google.android.material.snackbar.Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(resources.getColor(android.R.color.holo_red_dark, null))
                                .show()

                            binding.btnConfirmarReserva.isEnabled = true
                            gymViewModel.resetReservaStatus()
                        }
                    }
                }
            }
        }
    }

    private fun ejecutarProcesoReserva() {
        val horaSel = binding.spinnerHoras.text.toString()
        val fechaSel = binding.etFechaReserva.text.toString()

        if (horaSel.isNotEmpty() && horaSel != getString(R.string.prompt_selecciona_hora)) {
            val sesion = gymViewModel.listaSesiones.value.find { it.hora_inicio == horaSel }

            sesion?.let {
                val yaTiene = gymViewModel.listaMisReservas.value.any { r ->
                    r.fecha_sesion == fechaSel && r.hora_inicio == horaSel
                }

                if (yaTiene) {
                    com.google.android.material.snackbar.Snackbar.make(binding.root, getString(R.string.error_reserva_duplicada), com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
                        .setBackgroundTint(resources.getColor(android.R.color.holo_orange_dark, null)).show()
                } else {
                    binding.btnConfirmarReserva.isEnabled = false
                    gymViewModel.intentarReserva(it)
                }
            }
        } else {
            Toast.makeText(requireContext(), "Selecciona una hora válida", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeSesiones() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                gymViewModel.listaSesiones.collect { sesiones ->
                    val disponibles = sesiones.filter { it.plazas_ocupadas < it.capacidad_maxima }
                    if (disponibles.isEmpty()) {
                        binding.spinnerHoras.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, arrayOf("No hay disponibilidad")))
                        binding.btnConfirmarReserva.isEnabled = false
                    } else {
                        val horas = disponibles.map { it.hora_inicio }
                        binding.spinnerHoras.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, horas))
                        binding.spinnerHoras.setText(getString(R.string.prompt_selecciona_hora), false)
                        binding.btnConfirmarReserva.isEnabled = true
                    }
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val c = Calendar.getInstance()

        // Creamos el diálogo pero NO lo mostramos todavía con .show() al final
        val datePicker = DatePickerDialog(requireContext(), { _, y, m, d ->
            val cal = Calendar.getInstance().apply { set(y, m, d) }
            val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.time)
            binding.etFechaReserva.setText(fecha)

            // Al elegir fecha, pedimos al ViewModel que busque sesiones para ese día
            gymViewModel.cargarSesionesPorFecha(nombreActividad, fecha)
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

        // Establecemos que la fecha mínima seleccionable es "ahora"
        datePicker.datePicker.minDate = System.currentTimeMillis()

        // Ahora se muestra
        datePicker.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}