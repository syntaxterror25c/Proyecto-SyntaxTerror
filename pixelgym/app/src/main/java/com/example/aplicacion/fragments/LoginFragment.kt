package com.example.aplicacion.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.aplicacion.R
import com.example.aplicacion.databinding.FragmentLoginBinding
import com.example.aplicacion.viewmodels.AuthViewModel
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners(view)
        setupObservers()
    }

    private fun setupObservers() {
        // Tu observer original (habilitar botón)
        viewModel.isLoginValid.observe(viewLifecycleOwner) { isValid ->
            binding.buttonLogin.isEnabled = isValid
        }

        // ✅ NUEVO: observer del estado de login Firebase
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthViewModel.LoginState.Idle -> {
                    // nada
                }
                is AuthViewModel.LoginState.Loading -> {
                    setInputsEnabled(false)
                }
                is AuthViewModel.LoginState.Success -> {
                    setInputsEnabled(true)

                    val snack = Snackbar.make(binding.root, getString(R.string.login_success), Snackbar.LENGTH_LONG)
                    val tv = snack.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                    tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    snack.show()

                    findNavController().navigate(R.id.action_loginFragment_to_tabListRecursosFragment)
                }
                is AuthViewModel.LoginState.Error -> {
                    setInputsEnabled(true)

                    Snackbar.make(binding.root, getString(R.string.login_error), Snackbar.LENGTH_LONG).show()
                    binding.textInputLayoutPassword.error = getString(R.string.login_error_field)
                }
            }
        }
    }

    private fun setupListeners(view: View) {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.textInputLayoutPassword.error = null
                binding.textInputLayoutUsername.error = null
                val username = binding.editTextUsername.text.toString()
                val password = binding.editTextPassword.text.toString()

                viewModel.updateValidation(username, password)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.editTextUsername.addTextChangedListener(watcher)
        binding.editTextPassword.addTextChangedListener(watcher)

        // ✅ LOGIN: ahora llama al ViewModel (Firebase) y el observer decide éxito/error
        binding.buttonLogin.setOnClickListener {
            val username = binding.editTextUsername.text.toString()
            val password = binding.editTextPassword.text.toString()

            binding.textInputLayoutPassword.error = null
            viewModel.login(username, password)
        }

        // Gmail (igual)
        binding.buttonGmail.setOnClickListener {
            setInputsEnabled(false)
            val successSnackbar =
                Snackbar.make(binding.root, getString(R.string.feature_not_implemented), Snackbar.LENGTH_INDEFINITE)
            successSnackbar.setAction(getString(R.string.snackbar_action_close)) {
                setInputsEnabled(true)
                successSnackbar.dismiss()
            }
            successSnackbar.show()
        }

        // Registro (igual)
        binding.textViewRegisterLink.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun setInputsEnabled(enabled: Boolean) {
        binding.editTextUsername.isEnabled = enabled
        binding.editTextPassword.isEnabled = enabled
        binding.buttonLogin.isEnabled = if (enabled) viewModel.isLoginValid.value ?: false else false
        binding.buttonGmail.isEnabled = enabled
        binding.textViewRegisterLink.isClickable = enabled
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
