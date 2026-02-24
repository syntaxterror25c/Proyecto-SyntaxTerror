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
        // val context = holder.itemView.context

        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        val hoy = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.time

        val fechaReserva = try { sdf.parse(reserva.fecha_sesion) } catch (e: Exception) { null }
        val esPasada = fechaReserva != null && fechaReserva.before(hoy)

        // Lógica para el separador visual
        var mostrarSeparador = false
        if (esPasada) {
            if (position == 0) {
                mostrarSeparador = true
            } else {
                val anterior = listaReservas[position - 1]
                val fechaAnt = try { sdf.parse(anterior.fecha_sesion) } catch (e: Exception) { null }
                val anteriorEraPasada = fechaAnt != null && fechaAnt.before(hoy)
                // Solo se muestra si el anterior NO era pasado (punto de cambio)
                if (!anteriorEraPasada) mostrarSeparador = true
            }
        }

        holder.binding.apply {
            // Control del encabezado de sección
            sectionHeader.visibility = if (mostrarSeparador) android.view.View.VISIBLE else android.view.View.GONE

            // Datos básicos
            tvReservaNombre.text = reserva.nombre_actividad
            tvReservaFecha.text = reserva.fecha_sesion
            tvReservaHora.text = reserva.hora_inicio
            ivReservaImagen.setImageResource(ImageMapper.getDrawableId(reserva.imagen_url))

            if (esPasada) {
                // Estilo Historial. Afectamos a la tarjeta
                cardReserva.alpha = 0.5f
                btnAnular.visibility = android.view.View.GONE
                btnAnular.setOnClickListener(null)
            } else {
                // Estilo Activa
                cardReserva.alpha = 1.0f
                btnAnular.visibility = android.view.View.VISIBLE
                btnAnular.setOnClickListener { onAnularClick(reserva) }
            }
        }
    }

    override fun getItemCount(): Int = listaReservas.size
}