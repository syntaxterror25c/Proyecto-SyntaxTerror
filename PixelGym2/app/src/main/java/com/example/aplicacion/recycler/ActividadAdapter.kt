package com.example.aplicacion.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacion.R
import com.example.aplicacion.databinding.ItemActividadBinding
import com.bumptech.glide.Glide
import com.example.aplicacion.model.Actividad
import com.example.aplicacion.utils.ImageMapper

class ActividadAdapter(
    private var listaActividades: List<Actividad>,
    private val context: Context,
    private val onActividadClick: (Actividad) -> Unit
) : RecyclerView.Adapter<ActividadAdapter.ActividadViewHolder>() {

    inner class ActividadViewHolder(val binding: ItemActividadBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActividadViewHolder {
        val binding = ItemActividadBinding.inflate(LayoutInflater.from(context), parent, false)
        return ActividadViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActividadViewHolder, position: Int) {
        val actividad = listaActividades[position]

        // Nombre de la actividad
        holder.binding.tvActividadNombre.text = actividad.nombre

        // Categoría (He añadido esto para que coincida con el XML de la tarjeta)
        holder.binding.tvActividadCategoria.text = actividad.categoria.ifEmpty { "General" }

        // --- CARGA DE IMAGEN CENTRALIZADA ---
        val resourceId = ImageMapper.getDrawableId(actividad.imagen)

        Glide.with(context)
            .load(resourceId)
            .placeholder(R.drawable.im_rec_0default)
            .centerCrop()
            .into(holder.binding.ivActividadImagen)
        // ------------------------------------

        // Navegación al Fragment de Reserva
        holder.itemView.setOnClickListener {
            onActividadClick(actividad)
        }

        // El corazón se queda como está en el XML (gris por defecto)
    }

    override fun getItemCount(): Int = listaActividades.size

    // Función útil para cuando cambies de pestaña entre Todas y Favoritos
    fun updateData(nuevaLista: List<Actividad>) {
        listaActividades = nuevaLista
        notifyDataSetChanged()
    }
}