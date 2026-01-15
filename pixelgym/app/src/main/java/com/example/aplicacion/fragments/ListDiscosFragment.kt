package com.example.aplicacion.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.aplicacion.recycler.DiscoAdapter
import com.example.aplicacion.databinding.FragmentListDiscosBinding
import com.example.aplicacion.recycler.Disco
import com.example.aplicacion.viewmodels.ListViewModel

class ListDiscosFragment : Fragment() {

    private var _binding: FragmentListDiscosBinding? = null
    private val binding get() = _binding!!

    // activityViewModels() es para conseguir la instancia compartida del ViewModel
    private val listViewModel: ListViewModel by activityViewModels()

    private lateinit var discoAdapter: DiscoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListDiscosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // listener que llama al ViewModel para actualizar el fav
        discoAdapter = DiscoAdapter(
            mutableListOf(),
            { disco: Disco ->
                listViewModel.toggleFavStatus(disco)
                listViewModel.notifyDataChanged()
            },
            requireContext(),
            isFavoritesMode = false
        )

        // configurar el RecyclerView
        binding.recyclerViewDiscos.apply {
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