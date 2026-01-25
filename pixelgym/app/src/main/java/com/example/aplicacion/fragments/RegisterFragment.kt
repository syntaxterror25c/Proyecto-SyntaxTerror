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
import androidx.navigation.fragment.findNavController
import com.example.aplicacion.R
import com.example.aplicacion.databinding.FragmentRegisterBinding
import com.example.aplicacion.viewmodels.NewUserViewModel
import com.google.android.material.snackbar.Snackbar

// ✅ Firebase imports
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val registerViewModel: NewUserViewModel by viewModels()

    private var selectedPhotoUri: Uri? = null

    // ✅ Firebase instances
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPhotoUri = uri
            binding.ivUserPhoto.setImageURI(uri)
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

        setupTarifaDropdown()
        setupInputFilters()
        setupListeners()
        setupObservers()
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
        val nameFilter = InputFilter { source, _, _, _, _, _ ->
            val ok = source.all { it.isLetter() || it.isWhitespace() }
            if (ok) null else ""
        }
        binding.etRegName.filters = arrayOf(nameFilter)

        val phoneDigitsFilter = InputFilter { source, _, _, _, _, _ ->
            val ok = source.all { it.isDigit() }
            if (ok) null else ""
        }
        binding.etRegPhone.filters = arrayOf(
            phoneDigitsFilter,
            InputFilter.LengthFilter(9)
        )
    }

    private fun setupObservers() {
        registerViewModel.isRegisterValid.observe(viewLifecycleOwner) { enabled ->
            val btn = binding.btnCreateAccount
            btn.isEnabled = enabled

            if (enabled) {
                btn.setBackgroundColor(0xFF7B61FF.toInt())
                btn.setTextColor(0xFFFFFFFF.toInt())
                btn.alpha = 1f
            } else {
                btn.setBackgroundColor(0xFFE0E0E0.toInt())
                btn.setTextColor(0xFF777777.toInt())
                btn.alpha = 0.85f
            }
        }
    }

    private fun setupListeners() {

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

        binding.actvTarifa.setOnItemClickListener { _, _, _, _ ->
            binding.tilRegTarifa.error = null
            validateAll()
        }

        binding.btnPickPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // ✅ Botón "Crear cuenta" -> Firebase Auth + Firestore
        binding.btnCreateAccount.setOnClickListener {

            if (!showFieldErrorsIfNeeded()) {
                Snackbar.make(binding.root, getString(R.string.snackbar_invalid_data), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val email = binding.etRegEmail.text.toString().trim()
            val pass1 = binding.etRegPass.text.toString()
            val name = binding.etRegName.text.toString().trim()
            val phone = binding.etRegPhone.text.toString().trim()
            val tarifa = binding.actvTarifa.text.toString().trim()
            val photoUri = selectedPhotoUri?.toString()

            setInputsEnabled(false)

            createFirebaseUser(email, pass1, name, phone, tarifa, photoUri)
        }
    }

    private fun validateAll() {
        val email = binding.etRegEmail.text.toString().trim()
        val pass1 = binding.etRegPass.text.toString()
        val pass2 = binding.etRegPassConfirm.text.toString()
        val name = binding.etRegName.text.toString().trim()
        val phone = binding.etRegPhone.text.toString().trim()
        val tarifa = binding.actvTarifa.text.toString().trim()
        val photoSelected = true

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

    // ------------------------
    // ✅ Firebase helpers
    // ------------------------
    private fun createFirebaseUser(
        email: String,
        password: String,
        name: String,
        phone: String,
        tarifa: String,
        photoUri: String?
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser!!.uid
                saveUserProfile(uid, email, name, phone, tarifa, photoUri)
            }
            .addOnFailureListener { e ->
                setInputsEnabled(true)
                Snackbar.make(binding.root, "Registro falló: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
    }

    private fun saveUserProfile(
        uid: String,
        email: String,
        name: String,
        phone: String,
        tarifa: String,
        photoUri: String?
    ) {
        val userData = hashMapOf(
            "email" to email,
            "name" to name,
            "phone" to phone,
            "tarifa" to tarifa,
            "photoUri" to photoUri,
            "createdAt" to FieldValue.serverTimestamp()
        )

        db.collection("users").document(uid).set(userData)
            .addOnSuccessListener {
                setInputsEnabled(true)

                Snackbar.make(binding.root, getString(R.string.snackbar_account_created), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.snackbar_go_login)) {
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    }
                    .show()

                // Navegar al login automáticamente también
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
            .addOnFailureListener { e ->
                setInputsEnabled(true)
                Snackbar.make(binding.root, "Error guardando perfil: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
    }

    private fun setInputsEnabled(enabled: Boolean) {
        binding.etRegEmail.isEnabled = enabled
        binding.etRegPass.isEnabled = enabled
        binding.etRegPassConfirm.isEnabled = enabled
        binding.etRegName.isEnabled = enabled
        binding.etRegPhone.isEnabled = enabled
        binding.actvTarifa.isEnabled = enabled
        binding.btnPickPhoto.isEnabled = enabled
        binding.btnCreateAccount.isEnabled = if (enabled) (registerViewModel.isRegisterValid.value ?: false) else false
    }
}
