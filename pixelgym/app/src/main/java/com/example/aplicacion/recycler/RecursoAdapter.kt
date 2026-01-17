package com.example.aplicacion.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacion.R
import com.example.aplicacion.databinding.ItemRecursoBinding

class RecursoAdapter(
    private var listaRecursos: MutableList<Recurso>,
    private val onFavClick: (Recurso) -> Unit,
    private val onRecursoClick: (Recurso) -> Unit, // Esta función manejará el clic
    private val context: Context,
    private val isFavoritesMode: Boolean = false
) : RecyclerView.Adapter<RecursoAdapter.RecursoViewHolder>() {

    class RecursoViewHolder(val binding: ItemRecursoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecursoViewHolder {
        val binding = ItemRecursoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecursoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecursoViewHolder, position: Int) {
        val recurso = listaRecursos[position]
        val binding = holder.binding

        binding.tvNombreRecurso.text = recurso.nombre
        binding.tvTipoRecurso.text = recurso.tipo
        binding.ivRecurso.setImageResource(recurso.imagen)

        val iconFav = if (recurso.fav) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
        binding.btnFav.setImageResource(iconFav)

        binding.btnFav.setOnClickListener { onFavClick(recurso) }

        // Al hacer clic en la tarjeta, ejecutamos la función que nos pase el fragmento
        binding.root.setOnClickListener { onRecursoClick(recurso) }
    }

    override fun getItemCount(): Int = listaRecursos.size

    fun updateData(newList: MutableList<Recurso>) {
        listaRecursos = newList
        notifyDataSetChanged()
    }
}