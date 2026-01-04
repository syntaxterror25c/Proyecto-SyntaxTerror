package es.cifpcarlos3.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.cifpcarlos3.databinding.FragmentTabBinding
import es.cifpcarlos3.viewpager.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

// Fragment que contiene las dos pestañas: "Lista" y "Favoritos".
// Llegamos aquí DESPUÉS del login correcto.
class TabFragment : Fragment() {

    private var _binding: FragmentTabBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Adapter del ViewPager2, indicando que se encuentra dentro de este fragment.
        viewPagerAdapter = ViewPagerAdapter(this) //uso esto para manejar los fragments de las pestañas
        binding.viewPager.adapter = viewPagerAdapter //asigno el adapter al viewpager

        // Conecto TabLayout y ViewPager2. -----ESTO PONE LOS TITULOS A LAS PESTAÑAS-----navegacion entre pestañas
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) { //segun la posicion pone un texto u otro
                0 -> "Lista"      // luego puedes pasar esto a strings.xml
                1 -> "Favoritos"
                else -> ""
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
