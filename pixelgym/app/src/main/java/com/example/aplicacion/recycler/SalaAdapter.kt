package com.example.aplicacion.recycler

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacion.databinding.ItemSalaBinding

import android.content.Context
import android.media.SoundPool
import com.example.aplicacion.R

class SalaAdapter(
    private var listaSalas: MutableList<Sala>,
    private val onFavToggle: (sala: Sala) -> Unit,
    context: Context,
    // para bloquear el Switch en la pestaña Favoritos
    private val isFavoritesMode: Boolean = false
) : RecyclerView.Adapter<SalaAdapter.DiscoViewHolder>() {

    private val handler = Handler(Looper.getMainLooper())
    private val soundPool: SoundPool by lazy {
        SoundPool.Builder().setMaxStreams(2).build()
    }
    private var soundOnId: Int = 0
    private var soundOffId: Int = 0

    init {
        soundOnId = soundPool.load(context, R.raw.click, 1)
        soundOffId = soundPool.load(context, R.raw.pick, 1)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        soundPool.release()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoViewHolder {
        val binding = ItemSalaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiscoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiscoViewHolder, position: Int) {
        holder.bind(listaSalas[position])
    }

    override fun getItemCount(): Int = listaSalas.size


    inner class DiscoViewHolder(private val binding: ItemSalaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sala: Sala) {
            binding.tvSalaTitulo.text = sala.titulo
            binding.tvSalaAutor.text = sala.autor
            binding.tvSalaAno.text = sala.ano.toString()
            binding.ivSalaPortada.setImageResource(sala.imagenId)

            // Deshabilita el Switch si estamos en la pestaña Favoritos
            binding.switchFavorito.isEnabled = !isFavoritesMode
            // desactivar listener antes de establecer estado para evitar bucle
            binding.switchFavorito.setOnCheckedChangeListener(null)
            // estado inicial
            binding.switchFavorito.isChecked = sala.fav
            // volver a activar el listener con la lógica
            binding.switchFavorito.setOnCheckedChangeListener { _, isChecked ->
                if (binding.switchFavorito.isEnabled) { // Solo si está habilitado
                    if (isChecked) {
                        soundPool.play(soundOnId, 1f, 1f, 0, 0, 1f)
                    } else {
                        soundPool.play(soundOffId, 1f, 1f, 0, 0, 1f)
                    }

                    val position = bindingAdapterPosition

                    if (position != RecyclerView.NO_POSITION) {
                        handler.post {
                            // pasamos sala al Fragmento/ViewModel
                            onFavToggle(sala)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newSalas: MutableList<Sala>) {
        listaSalas = newSalas
        notifyDataSetChanged()
    }
}