package com.example.aplicacion.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacion.R
import com.example.aplicacion.databinding.ItemActividadBinding
import com.bumptech.glide.Glide

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

        holder.binding.tvActividadNombre.text = actividad.nombre
        // Si tienes un campo para el coste en el layout del catálogo:
        // holder.binding.tvCoste.text = "${actividad.coste} créditos"

        val resourceId = context.resources.getIdentifier(
            actividad.imagen,
            "drawable",
            context.packageName
        )

        Glide.with(context)
            .load(if (resourceId != 0) resourceId else R.drawable.im_rec_cardio)
            .centerCrop()
            .into(holder.binding.ivActividadImagen)

        holder.itemView.setOnClickListener {
            onActividadClick(actividad)
        }
    }

    override fun getItemCount(): Int = listaActividades.size
}