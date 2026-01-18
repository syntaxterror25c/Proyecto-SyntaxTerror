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

class ListRecursosFragment : Fragment() {

    private var _binding: FragmentListRecursosBinding? = null
    private val binding get() = _binding!!
    private val recursosViewModel: RecursosViewModel by activityViewModels()
    private lateinit var recursoAdapter: RecursoAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListRecursosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ahora pasamos exactamente los 5 parámetros que pide el nuevo Adapter
        recursoAdapter = RecursoAdapter(
            listaRecursos = mutableListOf(),
            onFavClick = { recurso ->
                recursosViewModel.toggleFavStatus(recurso)
            },
            onRecursoClick = { recurso ->
                // Al hacer clic, preparamos el bundle y navegamos
                val bundle = Bundle().apply {
                    putInt("recursoId", recurso.id)
                    putString("nombreRecurso", recurso.nombre)
                }
                findNavController().navigate(R.id.reservaFragment, bundle)
            },
            context = requireContext(),
            isFavoritesMode = false
        )

        binding.recyclerViewRecursos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recursoAdapter
        }

        recursosViewModel.listaRecursosMaster.observe(viewLifecycleOwner) {
            val listaProcesada = recursosViewModel.getProcessedList(onlyFavs = false)
            recursoAdapter.updateData(listaProcesada.toMutableList())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}