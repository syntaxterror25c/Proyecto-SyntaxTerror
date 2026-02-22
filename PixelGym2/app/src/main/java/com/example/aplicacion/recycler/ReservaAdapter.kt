package com.example.aplicacion.recycler

import com.example.aplicacion.model.Reserva
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacion.databinding.ItemReservaBinding
import com.example.aplicacion.utils.ImageMapper

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

            // Imagen
            val imageResId = ImageMapper.getDrawableId(reserva.imagen_url)
            ivReservaImagen.setImageResource(imageResId)

            btnAnular.setOnClickListener {
                onAnularClick(reserva)
            }
        }
    }

    override fun getItemCount(): Int = listaReservas.size
}