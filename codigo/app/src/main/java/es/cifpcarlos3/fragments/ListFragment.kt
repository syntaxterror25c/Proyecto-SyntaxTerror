package es.cifpcarlos3.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import es.cifpcarlos3.recycler.Serie
import es.cifpcarlos3.recycler.SerieAdapter
import es.cifpcarlos3.viewmodels.SerieViewModel
import es.cifpcarlos3.databinding.FragmentListBinding



/**
 * Fragment que muestra la LISTA COMPLETA de series.
 *
 * Este es el fragment de la primera pestaña ("Lista").
 * Aquí el usuario puede marcar y desmarcar favoritas.
 */
class ListFragment : Fragment() {

    // ---- VIEW BINDING ----
    // _binding puede ser null cuando la vista se destruye.
    private var _binding: FragmentListBinding? = null

    // binding nunca es null mientras el fragment está en pantalla.
    private val binding get() = _binding!!

    // ---- VIEWMODEL COMPARTIDO ----
    // Uso activityViewModels() porque este ViewModel se comparte
    // entre ListFragment y FavFragment (misma Activity).
    private val serieViewModel: SerieViewModel by activityViewModels()

    // Adapter del RecyclerView.
    private lateinit var adapter: SerieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?//importante guarda datos para poder restaurar la vista
    ): View {
        // Inflo el layout fragment_list.xml usando ViewBinding.
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ----- CREACIÓN DEL ADAPTER -----RECOGE DATOS PARA LA INTERFAZ GRAFICA
        // allowToggle = true -> aquí SÍ se puede cambiar el favorito.
        adapter = SerieAdapter(
            series = emptyList(),
            allowToggle = true
        ) { _: Int, serie: Serie, _: Boolean ->
            // Cuando se toca el CheckBox de una serie, aviso al ViewModel
            // para que cambie su estado de favorito.
            serieViewModel.toggleFavorito(serie)
        }

        // ----- CONFIGURACIÓN DEL RECYCLERVIEW -----
        binding.rvSeries.layoutManager = LinearLayoutManager(requireContext())// Para mostrar la vista en vertical
        binding.rvSeries.adapter = adapter // Asigno el adapter al RecyclerView

        // ----- OBSERVAR LA LISTA DE SERIES -----
        // Cada vez que cambie la lista en el ViewModel,
        // actualizo lo que muestra el adapter.
        serieViewModel.series.observe(viewLifecycleOwner) { lista ->
            adapter.updateData(lista)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpio el binding para evitar fugas de memoria.
        _binding = null
    }
}
