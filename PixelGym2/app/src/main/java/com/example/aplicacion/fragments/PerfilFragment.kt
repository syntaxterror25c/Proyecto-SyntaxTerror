package com.example.aplicacion.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.aplicacion.databinding.FragmentPerfilBinding
import com.example.aplicacion.firebase.ServiceLocator
import com.example.aplicacion.viewmodels.GymViewModel
import com.example.aplicacion.viewmodels.GymViewModelFactory
import kotlinx.coroutines.launch
import com.example.aplicacion.R

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    // Usamos activityViewModels para compartir el mismo ViewModel que el resto de la App
    private val gymViewModel: GymViewModel by activityViewModels {
        GymViewModelFactory(
            ServiceLocator.gymRepository,
            ServiceLocator.authRepository
        )
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1  Mandamos cargar los datos al entrar
        gymViewModel.cargarDatosUsuarioActual()

        // 2  Nos quedamos escuchando cambios
        observeUsuario()

        // 3  Configuración del botón de Cerrar Sesión
        binding.btnCerrarSesion.setOnClickListener {
            // Cerramos en Firebase
            com.example.aplicacion.firebase.ServiceLocator.authRepository.logout()

            // Navegamos al Login y limpiamos el historial para que no pueda volver atrás
            findNavController().navigate(
                R.id.loginFragment,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.nav_graph, true)
                    .build()
            )
        }
    }
    private fun observeUsuario() {
        viewLifecycleOwner.lifecycleScope.launch {
            // repeatOnLifecycle asegura que solo escuchemos datos cuando la pantalla es visible
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                gymViewModel.usuarioLogueado.collect { u ->
                    u?.let {
                        // 1 Datos principales (primer nivel del mapa)
                        binding.tvPerfilNombre.text = it["nombre_usuario"]?.toString() ?: "Usuario"
                        binding.tvPerfilEmail.text = it["email"]?.toString() ?: "Sin email"

                        // 2 Datos de la suscripción (mapa anidado)
                        val sub = it["suscripcion_actual"] as? Map<String, Any>
                        sub?.let { s ->
                            binding.tvPerfilCreditos.text = s["creditos"]?.toString() ?: "0"
                            binding.tvLabelPlan.text = "PLAN ${s["nombre_plan"]?.toString()?.uppercase() ?: "BÁSICO"}"
                            binding.tvPerfilEstado.text = s["estado_suscripcion"]?.toString() ?: "INACTIVA"

                            // Usamos la fecha de fin de plan que guardas en el registro
                            binding.tvPerfilInicio.text = s["fecha_fin_plan"]?.toString() ?: "--/--/----"

                            // Opcional: Color dinámico para el estado
                            if (s["estado_suscripcion"] == "ACTIVA") {
                                binding.tvPerfilEstado.setTextColor(android.graphics.Color.GREEN)
                            } else {
                                binding.tvPerfilEstado.setTextColor(android.graphics.Color.RED)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}