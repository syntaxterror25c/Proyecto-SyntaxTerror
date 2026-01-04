package es.cifpcarlos3.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import es.cifpcarlos3.databinding.FragmentFavBinding
import es.cifpcarlos3.recycler.SerieAdapter
import es.cifpcarlos3.viewmodels.SerieViewModel

/**
 * Fragment que muestra SOLO las series FAVORITAS.
 *
 * Este es el fragment de la segunda pestaña ("Favoritos").
 * Aquí NO se puede cambiar el estado de favorito:
 * solo se ven los elementos que se han marcado en ListFragment.
 */
class FavFragment : Fragment() {

    // ---- VIEW BINDING ----
    private var _binding: FragmentFavBinding? = null
    private val binding get() = _binding!!

    // ---- VIEWMODEL COMPARTIDO ----
    // Es el mismo SerieViewModel que se usa en ListFragment.
    private val serieViewModel: SerieViewModel by activityViewModels()

    // Adapter del RecyclerView de favoritos.
    private lateinit var adapter: SerieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflo el layout fragment_fav.xml
        _binding = FragmentFavBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ----- ADAPTER EN MODO SOLO LECTURA -----
        // allowToggle = false -> el CheckBox aparece, pero está BLOQUEADO.
        adapter = SerieAdapter(
            series = emptyList(),
            allowToggle = false
        ) { _, _, _ ->
            // Aquí no hago nada a propósito:
            // el enunciado dice que los favoritos sólo se cambian
            // desde la lista principal (ListFragment).
        }

        // ----- CONFIGURAR RECYCLERVIEW -----
        binding.rvFavSeries.layoutManager = LinearLayoutManager(requireContext())//Para mostrar la vista en vertical
        binding.rvFavSeries.adapter = adapter

        // ----- OBSERVAR LA LISTA DE SERIES -----
        // Cada vez que cambie algo en la lista general,
        // recalculo la lista SOLO de favoritas y se la doy al adapter.
        serieViewModel.series.observe(viewLifecycleOwner) {
            val favoritas = serieViewModel.obtenerFavoritas()
            adapter.updateData(favoritas)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
