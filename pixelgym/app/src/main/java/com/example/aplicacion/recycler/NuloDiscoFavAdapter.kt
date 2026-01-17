package com.example.aplicacion.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacion.databinding.ItemDiscoBinding

// SÓLO para la lista de favoritos.
class NuloDiscoFavAdapter(private var listaDiscos: MutableList<Disco>) :
    RecyclerView.Adapter<NuloDiscoFavAdapter.DiscoViewHolder>() {

    // inner class
    class DiscoViewHolder(private val binding: ItemDiscoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(disco: Disco) {
            // Asignar datos
            binding.tvDiscoTitulo.text = disco.titulo
            binding.tvDiscoAutor.text = disco.autor
            binding.tvDiscoAno.text = disco.ano.toString()

            // Configurar el estado del Switch (debe ser true)
            binding.switchFavorito.isChecked = disco.fav

            // bloquear switch
            binding.switchFavorito.isEnabled = false

            // Limpiar  Listener
            binding.switchFavorito.setOnCheckedChangeListener(null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoViewHolder {
        val binding = ItemDiscoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DiscoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiscoViewHolder, position: Int) {
        val discoActual = listaDiscos[position]
        holder.bind(discoActual)
    }

    override fun getItemCount(): Int {
        return listaDiscos.size
    }
}