package com.example.aplicacion.fragments

import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.aplicacion.R
import com.example.aplicacion.databinding.FragmentRegisterBinding
import com.example.aplicacion.viewmodels.NewUserViewModel
import com.google.android.material.snackbar.Snackbar

// Fragmento que representa la pantalla de REGISTRO.
// Aquí preparo el formulario con todos los campos y conecto la UI con el ViewModel.
class RegisterFragment : Fragment() {

    // ---------- VIEW BINDING ----------
    // Uso ViewBinding para acceder al layout fragment_register.xml sin findViewById.
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    // ---------- VIEWMODEL ----------
    // Uso el delegado viewModels() para obtener el ViewModel de forma limpia.
    private val registerViewModel: NewUserViewModel by viewModels()

    // ---------- FOTO ----------
    // Aquí guardo la Uri de la foto que el usuario elija.
    private var selectedPhotoUri: Uri? = null

    // Launcher moderno para elegir una imagen de la galería (sin pedir permisos raros).
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPhotoUri = uri
            // Pinto la imagen en el ImageView
            binding.ivUserPhoto.setImageURI(uri)
            // Revalido para activar el botón si procede
            validateAll()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Preparo el desplegable de tarifas (Standard / Duo / Premium)
        setupTarifaDropdown()

        // Pongo filtros típicos (nombre solo letras, teléfono solo números y 9 máx)
        setupInputFilters()

        // Escucho cambios en campos y clicks
        setupListeners()

        // Observo el LiveData para habilitar/deshabilitar el botón y cambiar su color
        setupObservers()

        // Validación inicial (botón desactivado)
        validateAll()
    }

    private fun setupTarifaDropdown() {
        val tarifas = listOf(
            getString(R.string.tarifa_standard),
            getString(R.string.tarifa_duo),
            getString(R.string.tarifa_premium)
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, tarifas)
        binding.actvTarifa.setAdapter(adapter)
    }

    private fun setupInputFilters() {
        // Nombre: solo letras y espacios (incluye acentos)
        val nameFilter = InputFilter { source, _, _, _, _, _ ->
            val ok = source.all { it.isLetter() || it.isWhitespace() }
            if (ok) null else ""
        }
        binding.etRegName.filters = arrayOf(nameFilter)

        // Teléfono: solo números + máximo 9 dígitos
        val phoneDigitsFilter = InputFilter { source, _, _, _, _, _ ->
            val ok = source.all { it.isDigit() }
            if (ok) null else ""
        }
        binding.etRegPhone.filters = arrayOf(
            phoneDigitsFilter,
            InputFilter.LengthFilter(9) // 👈 clave para que no acepte más de 9
        )
    }

    private fun setupObservers() {
        registerViewModel.isRegisterValid.observe(viewLifecycleOwner) { enabled ->
            val btn = binding.btnCreateAccount
            btn.isEnabled = enabled

            // Cambio de colores para que se note:
            if (enabled) {
                // Botón activo: morado + texto blanco
                btn.setBackgroundColor(0xFF7B61FF.toInt())
                btn.setTextColor(0xFFFFFFFF.toInt())
                btn.alpha = 1f
            } else {
                // Botón desactivado: gris + texto gris
                btn.setBackgroundColor(0xFFE0E0E0.toInt())
                btn.setTextColor(0xFF777777.toInt())
                btn.alpha = 0.85f
            }
        }
    }

    private fun setupListeners() {

        // Cuando escribo en cualquier campo, limpio errores y revalido
        val clearErrorsAndValidate = {
            binding.tilRegEmail.error = null
            binding.tilRegPass.error = null
            binding.tilRegPassConfirm.error = null
            binding.tilRegName.error = null
            binding.tilRegPhone.error = null
            binding.tilRegTarifa.error = null
            validateAll()
        }

        binding.etRegEmail.addTextChangedListener { clearErrorsAndValidate() }
        binding.etRegPass.addTextChangedListener { clearErrorsAndValidate() }
        binding.etRegPassConfirm.addTextChangedListener { clearErrorsAndValidate() }
        binding.etRegName.addTextChangedListener { clearErrorsAndValidate() }
        binding.etRegPhone.addTextChangedListener { clearErrorsAndValidate() }

        // Tarifa: cuando selecciono de la lista también revalido
        binding.actvTarifa.setOnItemClickListener { _, _, _, _ ->
            binding.tilRegTarifa.error = null
            validateAll()
        }

        // Botón "Seleccionar foto"
        binding.btnPickPhoto.setOnClickListener {
            // Esto abre el selector de imágenes
            pickImageLauncher.launch("image/*")
        }

        // Botón "Crear cuenta"
        binding.btnCreateAccount.setOnClickListener {

            // Si hay errores → muestro Snackbar + pinto errores en campos
            if (!showFieldErrorsIfNeeded()) {
                Snackbar.make(binding.root, getString(R.string.snackbar_invalid_data), Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // ✅ Todo correcto → Snackbar de éxito
            Snackbar.make(binding.root, getString(R.string.snackbar_account_created), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.snackbar_go_login)) {
                    // Vuelvo al login
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
                .show()
            // También navego automáticamente al login
            view?.findNavController()?.navigate(
                R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun validateAll() {
        val email = binding.etRegEmail.text.toString().trim()
        val pass1 = binding.etRegPass.text.toString()
        val pass2 = binding.etRegPassConfirm.text.toString()
        val name = binding.etRegName.text.toString().trim()
        val phone = binding.etRegPhone.text.toString().trim()
        val tarifa = binding.actvTarifa.text.toString().trim()
        val photoSelected = (selectedPhotoUri != null)

        // Le paso todo al ViewModel para que calcule si el botón debe estar activo
        registerViewModel.updateValidation(
            email = email,
            pass1 = pass1,
            pass2 = pass2,
            name = name,
            phone = phone,
            tarifa = tarifa,
            photoSelected = photoSelected
        )
    }

    /**
     * Devuelve true si todo es válido.
     * Si no lo es, pinto errores "típicos" en cada campo.
     */
    private fun showFieldErrorsIfNeeded(): Boolean {
        val email = binding.etRegEmail.text.toString().trim()
        val pass1 = binding.etRegPass.text.toString()
        val pass2 = binding.etRegPassConfirm.text.toString()
        val name = binding.etRegName.text.toString().trim()
        val phone = binding.etRegPhone.text.toString().trim()
        val tarifa = binding.actvTarifa.text.toString().trim()

        var ok = true

        if (!registerViewModel.isEmailValid(email)) {
            binding.tilRegEmail.error = getString(R.string.error_email)
            ok = false
        }

        if (!registerViewModel.isPasswordValid(pass1)) {
            binding.tilRegPass.error = getString(R.string.error_password_len)
            ok = false
        }

        if (!registerViewModel.isPasswordValid(pass2)) {
            binding.tilRegPassConfirm.error = getString(R.string.error_password_len)
            ok = false
        }

        if (pass1.isNotEmpty() && pass2.isNotEmpty() && !registerViewModel.passwordsMatch(pass1, pass2)) {
            val msg = getString(R.string.password_mismatch)
            binding.tilRegPass.error = msg
            binding.tilRegPassConfirm.error = msg
            ok = false
        }

        if (!registerViewModel.isNameValid(name)) {
            binding.tilRegName.error = getString(R.string.error_name_letters)
            ok = false
        }

        if (!registerViewModel.isPhoneValid(phone)) {
            binding.tilRegPhone.error = getString(R.string.error_phone_len)
            ok = false
        }

        if (!registerViewModel.isTarifaValid(tarifa)) {
            binding.tilRegTarifa.error = getString(R.string.error_tarifa)
            ok = false
        }

        return ok
    }
}
