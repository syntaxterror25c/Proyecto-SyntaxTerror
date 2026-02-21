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

        holder.binding.apply {
            // Ahora los IDs son claros y semánticos:
            tvReservaNombre.text = reserva.nombre_actividad
            tvReservaFecha.text = reserva.fecha_sesion
            tvReservaHora.text = reserva.hora_inicio

            // Si añadiste el profesor a la data class Reserva:
            // tvReservaProfesor.text = reserva.nombre_profesor

            btnAnular.setOnClickListener {
                onAnularClick(reserva)
            }
        }
    }

    override fun getItemCount(): Int = listaReservas.size
}