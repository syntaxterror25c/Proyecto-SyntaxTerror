package com.example.aplicacion.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.aplicacion.R
import com.example.aplicacion.databinding.FragmentDetalleActividadBinding
import com.example.aplicacion.viewmodels.GymViewModel

class DetalleActividadFragment : Fragment() {

    private var _binding: FragmentDetalleActividadBinding? = null
    private val binding get() = _binding!!

    // Usamos el ViewModel compartido
    private val gymViewModel: GymViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Usamos el nuevo layout renombrado
        _binding = FragmentDetalleActividadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getString("actividadId") ?: ""

        // Buscamos la actividad en el ViewModel por su ID
        // Como es StateFlow, accedemos a .value
        val actividad = gymViewModel.listaActividades.value.find { it.id == id }

        actividad?.let {
            binding.tvActividadNombreDetalle.text = it.nombre
            // Si en tu objeto Actividad tienes un campo 'descripcion', úsalo aquí:
            binding.tvActividadDescripcion.text = "Información técnica sobre ${it.nombre}. Intensidad alta, mejora cardiovascular y resistencia."

            // Cargar imagen con Glide
            val resourceId = requireContext().resources.getIdentifier(
                it.imagen, "drawable", requireContext().packageName
            )

            Glide.with(this)
                .load(if (resourceId != 0) resourceId else R.drawable.im_rec_cardio)
                .placeholder(R.drawable.im_rec_cardio)
                .into(binding.ivActividadDetalle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}