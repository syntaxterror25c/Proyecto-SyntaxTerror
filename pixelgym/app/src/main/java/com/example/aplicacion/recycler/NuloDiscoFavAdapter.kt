package com.example.aplicacion.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacion.databinding.ItemSalaBinding

// SÓLO para la lista de favoritos.
class NuloDiscoFavAdapter(private var listaSalas: MutableList<Sala>) :
    RecyclerView.Adapter<NuloDiscoFavAdapter.DiscoViewHolder>() {

    // inner class
    class DiscoViewHolder(private val binding: ItemSalaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sala: Sala) {
            // Asignar datos
            binding.tvSalaTitulo.text = sala.titulo
            binding.tvSalaAutor.text = sala.autor
            binding.tvSalaAno.text = sala.ano.toString()

            // Configurar el estado del Switch (debe ser true)
            binding.switchFavorito.isChecked = sala.fav

            // bloquear switch
            binding.switchFavorito.isEnabled = false

            // Limpiar  Listener
            binding.switchFavorito.setOnCheckedChangeListener(null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoViewHolder {
        val binding = ItemSalaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DiscoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiscoViewHolder, position: Int) {
        val discoActual = listaSalas[position]
        holder.bind(discoActual)
    }

    override fun getItemCount(): Int {
        return listaSalas.size
    }
}