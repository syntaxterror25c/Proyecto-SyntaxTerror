package com.example.aplicacion.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.aplicacion.databinding.FragmentReservaBinding
import com.bumptech.glide.Glide
import com.example.aplicacion.R
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ReservaFragment : Fragment() {
    private var _binding: FragmentReservaBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReservaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Recuperar datos que vienen de la Cartelera
        val nombreAct = arguments?.getString("nombreActividad") ?: ""
        val imgUrl = arguments?.getString("imagenUrl") ?: ""

        binding.tvReservaTitulo.text = nombreAct

        // Cargar imagen
        val resId = resources.getIdentifier(imgUrl, "drawable", requireContext().packageName)
        Glide.with(this)
            .load(if (resId != 0) resId else R.drawable.im_rec_cardio)
            .centerCrop()
            .into(binding.imgReservaActividad)

        // 2. Configurar el botón para abrir el Calendario
        binding.btnElegirFecha.setOnClickListener {
            abrirCalendario(nombreAct)
        }

        // 3. Botón de reserva (por ahora solo un mensaje)
        binding.btnConfirmarReserva.setOnClickListener {
            val hora = binding.spinnerHoras.selectedItem?.toString()
            val fecha = binding.tvFechaSeleccionada.text.toString()
            Toast.makeText(requireContext(), "Reservado $nombreAct a las $hora ($fecha)", Toast.LENGTH_LONG).show()
        }
    }

    private fun abrirCalendario(actividad: String) {
        val calendario = Calendar.getInstance()

        // Creamos el selector de fecha
        val dpd = DatePickerDialog(requireContext(), { _, year, month, day ->
            // Formato: d/m/yyyy (igual que en tu script de carga)
            val fechaSeleccionada = "$day/${month + 1}/$year"
            binding.tvFechaSeleccionada.text = "Fecha: $fechaSeleccionada"

            // Cuando el usuario elige fecha, buscamos en Firebase
            buscarHorasDisponibles(actividad, fechaSeleccionada)

        }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH))

        dpd.show()
    }

    private fun buscarHorasDisponibles(actividad: String, fecha: String) {
        // Buscamos en la colección "sesiones"
        db.collection("sesiones")
            .whereEqualTo("nombre_actividad", actividad)
            .whereEqualTo("fecha", fecha)
            .get()
            .addOnSuccessListener { documentos ->
                val listaHoras = mutableListOf<String>()

                for (doc in documentos) {
                    val hora = doc.getString("hora_inicio") ?: ""
                    listaHoras.add(hora)
                }

                if (listaHoras.isEmpty()) {
                    Toast.makeText(requireContext(), "No hay clases para este día", Toast.LENGTH_SHORT).show()
                    binding.btnConfirmarReserva.isEnabled = false
                    // Limpiamos el spinner si no hay nada
                    binding.spinnerHoras.adapter = null
                } else {
                    // Ordenamos las horas para que salgan bonitas
                    listaHoras.sort()

                    // Llenamos el Spinner con las horas encontradas
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listaHoras)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerHoras.adapter = adapter

                    // Activamos el botón de reserva
                    binding.btnConfirmarReserva.isEnabled = true
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}