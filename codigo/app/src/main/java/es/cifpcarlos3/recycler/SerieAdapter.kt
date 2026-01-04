package es.cifpcarlos3.recycler

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.cifpcarlos3.R

// Adapter del RecyclerView.
//
// Lo voy a usar en dos fragments distintos:
//  - ListFragment: allowToggle = true  -> se puede cambiar el favorito.
//  - FavFragment:  allowToggle = false -> el checkbox está BLOQUEADO.
//
class SerieAdapter(
    private var series: List<Serie>,
    private val allowToggle: Boolean,
    private val onFavoritoClick: (position: Int, serie: Serie, isChecked: Boolean) -> Unit
) : RecyclerView.Adapter<SerieAdapter.SerieViewHolder>() {

    // ViewHolder con las vistas del XML item_serie.
    inner class SerieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImagen: ImageView = itemView.findViewById(R.id.ivImagenSerie)
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTituloSerie)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcionSerie)
        val cbFavorito: CheckBox = itemView.findViewById(R.id.cbFavorito)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SerieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_serie, parent, false)
        return SerieViewHolder(view)
    }

    override fun getItemCount(): Int = series.size

    override fun onBindViewHolder(holder: SerieViewHolder, position: Int) {
        val serie = series[position]

        // Pinto los datos.
        holder.ivImagen.setImageResource(serie.imagenResId)
        holder.tvTitulo.text = serie.titulo
        holder.tvDescripcion.text = serie.descripcion
        holder.cbFavorito.isChecked = serie.esFavorita

        // Limpio listeners anteriores para que no se disparen solos.
        holder.cbFavorito.setOnCheckedChangeListener(null)
        holder.cbFavorito.setOnClickListener(null)

        if (allowToggle) {
            // MODO LISTA COMPLETA: se permite cambiar favoritos.
            holder.cbFavorito.isEnabled = true
            holder.cbFavorito.isClickable = true

            holder.cbFavorito.setOnCheckedChangeListener { _, isChecked ->
                // Aviso al ViewModel / Fragment de que ha cambiado el favorito.
                onFavoritoClick(position, serie, isChecked)

                // ---- REPRODUCIR SONIDO ----
                // Si la serie tiene sonido configurado, lo reproduzco.
                val soundResId = if (isChecked) serie.soundOnResId else serie.soundOffResId
                //Reproduzco el sonido si hay uno asignado
                if (soundResId != 0) {
                    MediaPlayer.create(holder.itemView.context, soundResId).apply {
                        setOnCompletionListener { mp -> mp.release() }
                        start()
                    }
                }
            }
        } else {
            // MODO FAVORITOS: el checkbox está bloqueado.
            holder.cbFavorito.isEnabled = false
            holder.cbFavorito.isClickable = false
        }
    }

    // Actualiza la lista cuando cambian los datos en el ViewModel.
    fun updateData(newSeries: List<Serie>) {
        series = newSeries
        notifyDataSetChanged()
    }
}
