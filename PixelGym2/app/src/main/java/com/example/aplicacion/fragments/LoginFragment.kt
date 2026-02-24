package com.example.aplicacion.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.aplicacion.R
import com.example.aplicacion.databinding.FragmentLoginBinding
import com.example.aplicacion.viewmodels.AuthViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import com.example.aplicacion.firebase.ServiceLocator
import com.example.aplicacion.viewmodels.UserUiState
import com.example.aplicacion.viewmodels.AuthViewModelFactory

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(ServiceLocator.authRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- PERSISTENCIA LOGIN ---
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            findNavController().navigate(R.id.action_loginFragment_to_tabListReservasFragment)
            return // OJO! Para no ejecutar el resto del código del login
        }

        // Escuchar el estado de la autenticación
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UserUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.buttonLogin.isEnabled = false
                        }
                        is UserUiState.Authenticated -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_loginFragment_to_tabListReservasFragment)
                        }
                        is UserUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.buttonLogin.isEnabled = true
                            Toast.makeText(requireContext(), getString(R.string.login_error), Toast.LENGTH_SHORT).show()                        }
                        else -> {
                            binding.progressBar.visibility = View.GONE
                            binding.buttonLogin.isEnabled = false
                        }
                    }
                }
            }
        }

        // Observar validación para activar/desactivar botón
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoginValid.collect { isValid ->
                    binding.buttonLogin.isEnabled = isValid
                }
            }
        }

        // Listeners de texto (Actualizan el ViewModel)
        binding.editTextUsername.doOnTextChanged { _, _, _, _ ->
            actualizarValidacion()
        }

        binding.editTextPassword.doOnTextChanged { _, _, _, _ ->
            actualizarValidacion()
        }

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextUsername.text.toString()
            val pass = binding.editTextPassword.text.toString()
            viewModel.login(email, pass)
        }
        binding.buttonGmail.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.feature_not_implemented), Toast.LENGTH_LONG).show()
        }
        binding.textViewRegisterLink.setOnClickListener {
            // Navegamos al fragmento de registro
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
           // Toast.makeText(requireContext(), getString(R.string.nav_to_register), Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarValidacion() {
        viewModel.updateValidation(
            binding.editTextUsername.text.toString(),
            binding.editTextPassword.text.toString()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}