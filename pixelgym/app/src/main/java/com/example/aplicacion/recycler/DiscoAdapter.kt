package com.example.aplicacion.recycler

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacion.databinding.ItemDiscoBinding

import android.content.Context
import android.media.SoundPool
import com.example.aplicacion.R

class DiscoAdapter(
    private var listaDiscos: MutableList<Disco>,
        private val onFavToggle: (disco: Disco) -> Unit,
    context: Context,
    // para bloquear el Switch en la pestaña Favoritos
    private val isFavoritesMode: Boolean = false
) : RecyclerView.Adapter<DiscoAdapter.DiscoViewHolder>() {

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
        val binding = ItemDiscoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiscoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiscoViewHolder, position: Int) {
        holder.bind(listaDiscos[position])
    }

    override fun getItemCount(): Int = listaDiscos.size


    inner class DiscoViewHolder(private val binding: ItemDiscoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(disco: Disco) {
            binding.tvDiscoTitulo.text = disco.titulo
            binding.tvDiscoAutor.text = disco.autor
            binding.tvDiscoAno.text = disco.ano.toString()
            binding.ivDiscoPortada.setImageResource(disco.imagenId)

            // Deshabilita el Switch si estamos en la pestaña Favoritos
            binding.switchFavorito.isEnabled = !isFavoritesMode
            // desactivar listener antes de establecer estado para evitar bucle
            binding.switchFavorito.setOnCheckedChangeListener(null)
            // estado inicial
            binding.switchFavorito.isChecked = disco.fav
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
                            // pasamos disco al Fragmento/ViewModel
                            onFavToggle(disco)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newDiscos: MutableList<Disco>) {
        listaDiscos = newDiscos
        notifyDataSetChanged()
    }
}