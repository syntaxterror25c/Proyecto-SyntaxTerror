package com.example.aplicacion.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.aplicacion.R
import com.example.aplicacion.databinding.FragmentRegisterBinding
import com.example.aplicacion.viewmodels.NewUserViewModel
import java.util.Calendar
import androidx.navigation.findNavController

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NewUserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[NewUserViewModel::class.java]

        setupListeners(view)
        setupObservers() // Escuchar los cambios del ViewModel
    }


     // Observación del LiveData en el ViewModel para actualizar interfaz si botón cambia
    private fun setupObservers() {
        // habilitar/deshabilitar  botón
        viewModel.isRegisterValid.observe(viewLifecycleOwner) { isValid ->
            binding.btnCreateAccount.isEnabled = isValid
        }
    }

    private fun setupListeners(view: View) {
        // lógica datePicker
        binding.etRegDate.setOnClickListener {
            showDatePicker()
        }

        // watcher para  notificar al ViewModel
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.tilRegUser.error = null
                binding.tilRegPass.error = null
                binding.tilRegPassConfirm.error = null
                val user = binding.etRegUser.text.toString()
                val pass1 = binding.etRegPass.text.toString()
                val pass2 = binding.etRegPassConfirm.text.toString()
                val date = binding.etRegDate.text.toString()

                // llamar viewModel para validar y actualizar  LiveData
                viewModel.updateValidation(user, pass1, pass2, date)
            }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
        }

        // listeners
        binding.etRegUser.addTextChangedListener(watcher)
        binding.etRegPass.addTextChangedListener(watcher)
        binding.etRegPassConfirm.addTextChangedListener(watcher)
        binding.etRegDate.addTextChangedListener(watcher)

        // Botón Crear Cuenta
        binding.btnCreateAccount.setOnClickListener {
            val pass1 = binding.etRegPass.text.toString()
            val pass2 = binding.etRegPassConfirm.text.toString()

            // Limpiar errores previos
            binding.tilRegPass.error = null
            binding.tilRegPassConfirm.error = null

            if (viewModel.passwordsMatch(pass1, pass2)) {

                val successSnackbar = com.google.android.material.snackbar.Snackbar.make(
                    binding.root,
                    getString(R.string.register_success_message),
                    com.google.android.material.snackbar.Snackbar.LENGTH_INDEFINITE
                )
                successSnackbar.setAction(getString(R.string.snackbar_action_close)) {
                    successSnackbar.dismiss()
                    // OJO. Navegar de vuelta al login
                    view.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
                successSnackbar.show()
            } else {
                // Mostrar error en los campos
                val errorMsg = getString(R.string.password_mismatch)
                binding.tilRegPass.error = errorMsg
                binding.tilRegPassConfirm.error = errorMsg
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val dateString = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.etRegDate.setText(dateString)

                // después de seleccionar fecha forzamos la re-validación en el ViewModel
                val user = binding.etRegUser.text.toString()
                val pass1 = binding.etRegPass.text.toString()
                val pass2 = binding.etRegPassConfirm.text.toString()
                viewModel.updateValidation(user, pass1, pass2, dateString)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}