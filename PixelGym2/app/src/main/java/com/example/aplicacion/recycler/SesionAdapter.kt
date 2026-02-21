package com.example.aplicacion.recycler

import android.annotation.SuppressLint
import android.content.Context
import android.media.SoundPool
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacion.R
import com.example.aplicacion.databinding.ItemSesionBinding
import com.bumptech.glide.Glide
import com.example.aplicacion.model.Sesion

class SesionAdapter(
    private var listaSesiones: MutableList<Sesion>,
    private val onReservaClick: (sesion: Sesion) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<SesionAdapter.SesionViewHolder>() {

    private val soundPool: SoundPool by lazy {
        SoundPool.Builder().setMaxStreams(2).build()
    }
    private var soundClickId: Int = soundPool.load(context, R.raw.click, 1)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SesionViewHolder {
        val binding = ItemSesionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SesionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SesionViewHolder, position: Int) {
        holder.bind(listaSesiones[position])
    }

    override fun getItemCount(): Int = listaSesiones.size

    inner class SesionViewHolder(private val binding: ItemSesionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sesion: Sesion) {
            binding.tvActividadNombre.text = sesion.nombre_actividad.ifEmpty { "Actividad" }
            binding.tvProfesorNombre.text = sesion.nombre_profesor
            binding.tvHoraInicio.text = sesion.hora_inicio.ifEmpty { "--:--" }

            if (sesion.capacidad_maxima > 0) {
                val disponibles = sesion.capacidad_maxima - sesion.plazas_ocupadas
                binding.tvPlazasLibres.text = "Plazas: $disponibles"
                binding.tvPlazasLibres.visibility = View.VISIBLE
            } else {
                binding.tvPlazasLibres.visibility = View.GONE
            }

            // --- MEJORA DE CARGA DE IMAGEN ---
            val resourceId = if (sesion.imagen_url.isNotEmpty()) {
                context.resources.getIdentifier(
                    sesion.imagen_url,
                    "drawable",
                    context.packageName
                )
            } else 0

            Glide.with(context)
                .load(if (resourceId != 0) resourceId else R.drawable.im_rec_cardio)
                .placeholder(R.drawable.im_rec_cardio) // Imagen mientras carga
                .error(R.drawable.im_rec_cardio)       // Imagen si falla el ID
                .centerCrop()
                .into(binding.ivSesionImagen)
            // ---------------------------------

            binding.btnReservar.setOnClickListener {
                soundPool.play(soundClickId, 1f, 1f, 0, 0, 1f)
                onReservaClick(sesion)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(nuevaLista: List<Sesion>) {
        listaSesiones.clear()
        listaSesiones.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}