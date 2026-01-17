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
import com.example.aplicacion.databinding.FragmentReservaBinding
import com.example.aplicacion.viewmodels.RecursosViewModel
import java.util.*

class ReservaFragment : Fragment() {

    private var _binding: FragmentReservaBinding? = null
    private val binding get() = _binding!!

    private val recursosViewModel: RecursosViewModel by activityViewModels()
    private var recursoIdSeleccionado: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReservaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recursoIdSeleccionado = arguments?.getInt("recursoId") ?: -1
        val nombreRecurso = arguments?.getString("nombreRecurso") ?: "Recurso"
        binding.tvNombreRecursoReserva.text = nombreRecurso

        // Restaurar estado si el usuario ya había seleccionado algo antes de navegar fuera
        restaurarEstadoPrevio()

        binding.etFechaReserva.setOnClickListener { showDatePickerDialog() }

        binding.btnConfirmarReserva.setOnClickListener {
            val fechaText = binding.etFechaReserva.text.toString()
            val horaSeleccionada = binding.spinnerHoras.text.toString()

            if (recursoIdSeleccionado != -1 && fechaText.isNotEmpty() && horaSeleccionada.isNotEmpty()) {
                val usuarioIdActual = 1
                val exito = recursosViewModel.confirmarReserva(recursoIdSeleccionado, usuarioIdActual, fechaText, horaSeleccionada)

                if (exito) {
                    Toast.makeText(requireContext(), "Reserva guardada", Toast.LENGTH_SHORT).show()
                    // Limpiamos los datos temporales del VM al terminar con éxito
                    recursosViewModel.limpiarDatosTemporales()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Error al guardar", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun restaurarEstadoPrevio() {
        // Obtenemos lo que haya en el ViewModel (debes tener estas variables allí)
        val fechaGuardada = recursosViewModel.fechaTemporal
        val horaGuardada = recursosViewModel.horaTemporal

        if (fechaGuardada.isNotEmpty()) {
            binding.etFechaReserva.setText(fechaGuardada)
            actualizarHorasDisponibles(fechaGuardada)

            if (horaGuardada.isNotEmpty()) {
                binding.spinnerHoras.setText(horaGuardada, false)
            }
        }
    }

    private fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        val dpd = DatePickerDialog(requireContext(), { _, y, m, d ->
            val fechaFormateada = "$d/${m + 1}/$y"
            binding.etFechaReserva.setText(fechaFormateada)

            // Guardamos en el VM para que no se pierda al cambiar de Fragment
            recursosViewModel.fechaTemporal = fechaFormateada
            recursosViewModel.horaTemporal = "" // Reset hora al cambiar fecha

            actualizarHorasDisponibles(fechaFormateada)
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        dpd.show()
    }

    private fun actualizarHorasDisponibles(fecha: String) {
        val horasLibres = recursosViewModel.obtenerHorasLibres(recursoIdSeleccionado, fecha)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            horasLibres
        )

        binding.spinnerHoras.setAdapter(adapter)

        // Escuchamos cuando el usuario elige una hora para guardarla en el VM
        binding.spinnerHoras.setOnItemClickListener { _, _, position, _ ->
            recursosViewModel.horaTemporal = adapter.getItem(position) ?: ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}