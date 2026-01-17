package com.example.aplicacion.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicacion.databinding.FragmentListSalasBinding
import com.example.aplicacion.recycler.SalaAdapter
import com.example.aplicacion.viewmodels.ListViewModel
import com.example.aplicacion.recycler.Sala

class ListSalasFavFragment : Fragment() {

    private var _binding: FragmentListSalasBinding? = null
    private val binding get() = _binding!!

    // activityViewModels() es para conseguir la instancia compartida del ViewModel
    private val listViewModel: ListViewModel by activityViewModels()

    // SalaAdapter único
    private lateinit var salaAdapter: SalaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListSalasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar Adapter en modo FAVORITOS
        salaAdapter = SalaAdapter(
            mutableListOf(),
            // recibir sala
            { sala: Sala ->
                listViewModel.toggleFavStatus(sala)
                listViewModel.notifyDataChanged()
            },
            requireContext(),
            isFavoritesMode = true // bloquear switch por modo FAVORITOS
        )

        binding.recyclerViewSalas.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = salaAdapter
        }

// observer livedata
        listViewModel.listaDiscosMaster.observe(viewLifecycleOwner) { _ ->
            // Pedimos lista filtrada con fav y ordenada y con search en su caso
            val listaParaMostrar = listViewModel.getProcessedList(onlyFavs = true)

            // .post para asegurar la estabilidad en la actualización del RecyclerView
            binding.recyclerViewSalas.post {
                salaAdapter.updateData(listaParaMostrar.toMutableList())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}