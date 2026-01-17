package com.example.aplicacion.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.aplicacion.recycler.SalaAdapter
import com.example.aplicacion.databinding.FragmentListSalasBinding
import com.example.aplicacion.recycler.Sala
import com.example.aplicacion.viewmodels.ListViewModel

class ListSalasFragment : Fragment() {

    private var _binding: FragmentListSalasBinding? = null
    private val binding get() = _binding!!

    // activityViewModels() es para conseguir la instancia compartida del ViewModel
    private val listViewModel: ListViewModel by activityViewModels()

    private lateinit var discoAdapter: SalaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListSalasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // listener que llama al ViewModel para actualizar el fav
        discoAdapter = SalaAdapter(
            mutableListOf(),
            { sala: Sala ->
                listViewModel.toggleFavStatus(sala)
                listViewModel.notifyDataChanged()
            },
            requireContext(),
            isFavoritesMode = false
        )

        // configurar el RecyclerView
        binding.recyclerViewSalas.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = discoAdapter
        }

        // observación LIVEDATA
        listViewModel.listaDiscosMaster.observe(viewLifecycleOwner) { _ ->
            // Pedimos al ViewModel la lista procesada (con filtros de búsqueda y orden)
            val listaParaMostrar = listViewModel.getProcessedList(onlyFavs = false)

            // Actualizamos el adapter con el resultado
            discoAdapter.updateData(listaParaMostrar.toMutableList())
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}