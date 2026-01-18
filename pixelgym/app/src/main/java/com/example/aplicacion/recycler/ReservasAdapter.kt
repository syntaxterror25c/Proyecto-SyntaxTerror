package com.example.aplicacion.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacion.R
import com.example.aplicacion.viewmodels.Reserva

class ReservasAdapter(private var reservas: List<Reserva>) :
    RecyclerView.Adapter<ReservasAdapter.ReservaViewHolder>() {

    class ReservaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreRecurso)
        val tvFecha: TextView = view.findViewById(R.id.tvFechaReserva)
        val tvHora: TextView = view.findViewById(R.id.tvHoraReserva)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reserva, parent, false)
        return ReservaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        val reserva = reservas[position]
        holder.tvNombre.text = reserva.recursoNombre
        holder.tvFecha.text = reserva.fecha
        holder.tvHora.text = reserva.hora
    }

    override fun getItemCount() = reservas.size

    fun updateList(newList: List<Reserva>) {
        this.reservas = newList
        notifyDataSetChanged()
    }
}