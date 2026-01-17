package com.example.aplicacion.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicacion.R
import com.example.aplicacion.databinding.FragmentListRecursosBinding
import com.example.aplicacion.recycler.RecursoAdapter
import com.example.aplicacion.viewmodels.RecursosViewModel

class ListRecursosFavFragment : Fragment() {

    private var _binding: FragmentListRecursosBinding? = null
    private val binding get() = _binding!!
    private val recursosViewModel: RecursosViewModel by activityViewModels()
    private lateinit var recursoAdapter: RecursoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Usamos el mismo layout que el de la lista general
        _binding = FragmentListRecursosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializamos el adaptador con los 5 parámetros correctos
        recursoAdapter = RecursoAdapter(
            listaRecursos = mutableListOf(),
            onFavClick = { recurso ->
                recursosViewModel.toggleFavStatus(recurso)
                // No hace falta llamar a notifyDataChanged aquí si el ViewModel ya lo hace
            },
            onRecursoClick = { recurso ->
                // Navegación a reserva pasando datos por Bundle
                val bundle = Bundle().apply {
                    putInt("recursoId", recurso.id)
                    putString("nombreRecurso", recurso.nombre)
                }
                findNavController().navigate(R.id.reservaFragment, bundle)
            },
            context = requireContext(),
            isFavoritesMode = true // Modo favoritos activado
        )

        binding.recyclerViewSalas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recursoAdapter
        }

        // Observamos los cambios en la lista maestra
        recursosViewModel.listaRecursosMaster.observe(viewLifecycleOwner) {
            // Filtramos solo los favoritos
            val listaParaMostrar = recursosViewModel.getProcessedList(onlyFavs = true)
            recursoAdapter.updateData(listaParaMostrar.toMutableList())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}