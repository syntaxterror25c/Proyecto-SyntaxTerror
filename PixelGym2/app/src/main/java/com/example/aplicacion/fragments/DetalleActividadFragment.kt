package com.example.aplicacion.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.aplicacion.databinding.FragmentDetalleActividadBinding
import com.example.aplicacion.utils.ImageMapper
import com.example.aplicacion.viewmodels.GymViewModel
// AÑADIMOS ESTA IMPORTACIÓN
import com.google.firebase.firestore.FirebaseFirestore

class DetalleActividadFragment : Fragment() {

    private var _binding: FragmentDetalleActividadBinding? = null
    private val binding get() = _binding!!
    private val gymViewModel: GymViewModel by activityViewModels()

    // Instancia de Firestore
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetalleActividadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getString("actividadId") ?: ""
        val actividad = gymViewModel.listaActividades.value.find { it.id == id }

        actividad?.let { itActividad ->
            binding.tvActividadNombreDetalle.text = itActividad.nombre
            binding.tvActividadCategoria.text = itActividad.categoria.ifEmpty { "General" }
            binding.tvActividadDescripcion.text = itActividad.descripcion.ifEmpty { "Sin descripción" }
            binding.chipCreditos.text = "${itActividad.coste} créditos"

            val resourceId = ImageMapper.getDrawableId(itActividad.imagen)
            com.bumptech.glide.Glide.with(this)
                .load(resourceId)
                .centerCrop()
                .into(binding.ivActividadDetalle)

            // --- NUEVO BLOQUE: CONSULTA DE SESIONES ---
            db.collection("sesiones")
                .whereEqualTo("nombre_actividad", itActividad.nombre)
                .get()
                .addOnSuccessListener { documentos ->
                    val listaHoras = documentos.mapNotNull { it.getString("hora_inicio") }
                        .distinct()
                        .sorted()

                    val listaProfes = documentos.mapNotNull { it.getString("nombre_profesor") }
                        .distinct()

                    if (!documentos.isEmpty) {
                        binding.tvHorariosDetalle.text = listaHoras.joinToString(" - ")
                        binding.tvProfesoresDetalle.text = listaProfes.joinToString(", ")
                    } else {
                        binding.tvHorariosDetalle.text = "Próximamente"
                        binding.tvProfesoresDetalle.text = "Consultar equipo"
                    }
                }
                .addOnFailureListener {
                    binding.tvHorariosDetalle.text = "Error al cargar"
                    binding.tvProfesoresDetalle.text = "Error al cargar"
                }
            // ------------------------------------------
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}