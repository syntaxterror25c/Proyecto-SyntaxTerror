package com.example.aplicacion.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.aplicacion.R
import com.example.aplicacion.databinding.FragmentRegisterBinding
import com.example.aplicacion.viewmodels.NewUserUiState
import com.example.aplicacion.firebase.ServiceLocator
import com.example.aplicacion.viewmodels.NewUserViewModel
import com.example.aplicacion.viewmodels.NewUserViewModelFactory
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewUserViewModel by viewModels {
        NewUserViewModelFactory(ServiceLocator.authRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Iniciamos la carga de planes y observamos el StateFlow
        observarPlanes()
        viewModel.cargarPlanes()

        // 2. Observar estado del registro (UI State)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is NewUserUiState.Loading -> binding.btnCreateAccount.isEnabled = false
                        is NewUserUiState.Created -> {
                            Toast.makeText(requireContext(), "¡Bienvenido a PixelGym!", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        }
                        is NewUserUiState.Error -> {
                            binding.btnCreateAccount.isEnabled = true
                            binding.tilRegUser.error = getString(state.messageRes)
                        }
                        else -> { /* Idle */ }
                    }
                }
            }
        }

        // 3. Observar validez del formulario
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isRegisterValid.collect { isValid ->
                    binding.btnCreateAccount.isEnabled = isValid
                }
            }
        }

        setupListeners()

        // 4. Acción de registro con DATOS REALES
        binding.btnCreateAccount.setOnClickListener {
            val email = binding.etRegUser.text.toString()
            val pass = binding.etRegPass.text.toString()
            val nombre = binding.etRegName.text.toString()

            // Cogemos la tarifa seleccionada
            val tarifaSeleccionada = binding.spinnerPlan.selectedItem as? com.example.aplicacion.models.Tarifa

            if (tarifaSeleccionada != null) {
                // Registro SIN teléfono (solo 4 parámetros)
                viewModel.register(email, pass, nombre, tarifaSeleccionada)
            } else {
                Toast.makeText(requireContext(), "Selecciona un plan primero", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- FUNCIONES DE APOYO (FUERA DE ONVIEWCREATED) ---
    private fun observarPlanes() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tarifas.collect { lista ->
                    if (lista.isNotEmpty()) {
                        // requireContext() está bien, pero si usas el estilo del Spinner
                        // personalizado, esto lo aplicará correctamente.
                        val adapter = ArrayAdapter(
                            requireContext(),
                            R.layout.item_spinner, // Diseño del item cerrado
                            lista
                        )

                        // Si quieres que el desplegable se vea igual de bien:
                        adapter.setDropDownViewResource(R.layout.item_spinner)

                        binding.spinnerPlan.adapter = adapter
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.etRegUser.doOnTextChanged { _, _, _, _ -> actualizarValidacion() }
        binding.etRegName.doOnTextChanged { _, _, _, _ -> actualizarValidacion() }
        binding.etRegPass.doOnTextChanged { _, _, _, _ -> actualizarValidacion() }
        binding.etRegPassConfirm.doOnTextChanged { _, _, _, _ -> actualizarValidacion() }
    }

    private fun actualizarValidacion() {
        val email = binding.etRegUser.text.toString()
        val nombre = binding.etRegName.text.toString()
        val pass = binding.etRegPass.text.toString()
        val confirm = binding.etRegPassConfirm.text.toString()

        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        binding.tilRegUser.error = if (email.isNotEmpty() && !email.matches(emailPattern.toRegex())) "Formato inválido" else null
        binding.tilRegPass.error = if (pass.isNotEmpty() && pass.length < 6) "Mínimo 6 caracteres" else null
        binding.tilRegPassConfirm.error = if (confirm.isNotEmpty() && confirm != pass) "No coinciden" else null

        viewModel.updateValidation(email, pass, confirm, nombre)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}