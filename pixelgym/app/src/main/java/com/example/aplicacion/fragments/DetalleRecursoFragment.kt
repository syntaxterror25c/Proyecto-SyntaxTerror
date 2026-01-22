package com.example.aplicacion.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.aplicacion.R
import com.example.aplicacion.data.RecursosRepositorio


class DetalleRecursoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_detalle_recurso, container, false)

        val recursoId = arguments?.getInt("recursoId", -1)

        val repo = RecursosRepositorio()
        val recurso = repo.getRecursosIniciales().find { it.id == recursoId }

        val tv = view.findViewById<TextView>(R.id.tvDetallePrueba)

        tv.text = recurso?.nombre ?: "Recurso no encontrado"

        val iv = view.findViewById<ImageView>(R.id.ivRecursoDetalle)
        recurso?.let {
            iv.setImageResource(it.imagen)
        }

        val tvDetalles = view.findViewById<TextView>(R.id.tvTextoDetalles)
        tvDetalles.text = recurso?.textoDetalles ?: ""

        val layoutHorarios = view.findViewById<LinearLayout>(R.id.layoutHorarios)

        val horarios = repo.getDisponibilidadParaRecurso(recursoId ?: -1, "2025-01-01")

        horarios.forEach { hora ->
            val tvHora = TextView(requireContext())
            tvHora.text = hora
            tvHora.textSize = 14f
            tvHora.setPadding(0, 8, 0, 8)
            layoutHorarios.addView(tvHora)
        }


        return view
    }
}
