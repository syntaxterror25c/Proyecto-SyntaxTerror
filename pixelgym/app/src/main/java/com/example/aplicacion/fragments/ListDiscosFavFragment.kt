package com.example.aplicacion.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicacion.databinding.FragmentListDiscosBinding
import com.example.aplicacion.recycler.DiscoAdapter
import com.example.aplicacion.viewmodels.ListViewModel
import com.example.aplicacion.recycler.Disco

class ListDiscosFavFragment : Fragment() {

    private var _binding: FragmentListDiscosBinding? = null
    private val binding get() = _binding!!

    // activityViewModels() es para conseguir la instancia compartida del ViewModel
    private val listViewModel: ListViewModel by activityViewModels()

    // DiscoAdapter único
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

        // Inicializar Adapter en modo FAVORITOS
        discoAdapter = DiscoAdapter(
            mutableListOf(),
            // recibir disco
            { disco: Disco ->
                listViewModel.toggleFavStatus(disco)
                listViewModel.notifyDataChanged()
            },
            requireContext(),
            isFavoritesMode = true // bloquear switch por modo FAVORITOS
        )

        binding.recyclerViewDiscos.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = discoAdapter
        }

// observer livedata
        listViewModel.listaDiscosMaster.observe(viewLifecycleOwner) { _ ->
            // Pedimos lista filtrada con fav y ordenada y con search en su caso
            val listaParaMostrar = listViewModel.getProcessedList(onlyFavs = true)

            // .post para asegurar la estabilidad en la actualización del RecyclerView
            binding.recyclerViewDiscos.post {
                discoAdapter.updateData(listaParaMostrar.toMutableList())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}