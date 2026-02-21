package com.example.aplicacion.recycler

import com.example.aplicacion.model.Reserva
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacion.databinding.ItemReservaBinding

class ReservaAdapter(
    private val listaReservas: List<Reserva>,
    private val onAnularClick: (Reserva) -> Unit
) : RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder>() {

    inner class ReservaViewHolder(val binding: ItemReservaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val binding = ItemReservaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReservaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        val reserva = listaReservas[position]
        val context = holder.itemView.context

        holder.binding.apply {
            // IDs de texto (estos ya te funcionaban)
            tvReservaNombre.text = reserva.nombre_actividad
            tvReservaFecha.text = reserva.fecha_sesion
            tvReservaHora.text = reserva.hora_inicio

            // --- LÓGICA PARA LA IMAGEN (Usando tu ID real: ivReservaImagen) ---
            val imageResId = context.resources.getIdentifier(
                reserva.imagen_url,
                "drawable",
                context.packageName
            )

            if (imageResId != 0) {
                ivReservaImagen.setImageResource(imageResId)
            } else {
                // Imagen por defecto si reserva.imagen_url está vacío o no existe
                ivReservaImagen.setImageResource(com.example.aplicacion.R.drawable.im_rec_cardio)
            }

            btnAnular.setOnClickListener {
                onAnularClick(reserva)
            }
        }
    }

    override fun getItemCount(): Int = listaReservas.size
}