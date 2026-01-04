package es.cifpcarlos3.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import es.cifpcarlos3.R
import es.cifpcarlos3.databinding.FragmentRegisterBinding
import es.cifpcarlos3.viewmodels.RegisterViewModel
import java.util.Calendar

// Fragmento que representa la pantalla de REGISTRO.
// Aquí preparo el formulario con todos los campos y conecto la parte visual
// (EditText, botones, etc.) con el ViewModel.
class RegisterFragment : Fragment() {

    // ---------- VIEW BINDING ----------
    // Uso ViewBinding para acceder a las vistas del layout fragment_register.xml.
    // _binding puede ser null cuando la vista ya se ha destruido.
    private var _binding: FragmentRegisterBinding? = null

    // Esta propiedad nunca debería ser null mientras la vista está viva.
    private val binding get() = _binding!!

    // ---------- VIEWMODEL ----------
    // ViewModel específico para el registro. Contiene las reglas de validación.
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflo el layout usando el binding, no con inflate(R.layout.fragment_register)
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ---------- REFERENCIAS A LOS CAMPOS ----------
        // A partir del binding accedo a todos los widgets del XML.
        val etUser = binding.etUserReg
        val etEmail = binding.etEmailReg
        val etPass = binding.etPassReg
        val etPassConfirm = binding.etPassConfirmReg
        val etBirth = binding.etBirthReg
        val btnCreate = binding.btnCreateAccount

        // ---------- TEXTWATCHER COMÚN ----------
        // Cada vez que cambia cualquier campo de texto, llamo a onFieldsChanged
        // del ViewModel para que revise si el botón debe estar activo o no.
        val watcher = {
            registerViewModel.onFieldsChanged(
                etUser.text.toString(),
                etEmail.text.toString(),
                etPass.text.toString(),
                etPassConfirm.text.toString(),
                etBirth.text.toString()
            )
        }

        // Asocio el watcher a todos los EditText.
        etUser.addTextChangedListener { watcher() }
        etEmail.addTextChangedListener { watcher() }
        etPass.addTextChangedListener { watcher() }
        etPassConfirm.addTextChangedListener { watcher() }
        etBirth.addTextChangedListener { watcher() }

        // ---------- DATE PICKER PARA FECHA DE NACIMIENTO ----------
        // Cuando el usuario pulsa en el campo de fecha, abro un DatePickerDialog
        // y al elegir la fecha la escribo en el EditText.
        etBirth.setOnClickListener {
            // Fecha actual como valor inicial.
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Creo el diálogo.
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    // Formateo sencillo: día/mes/año.
                    val fecha = "$d/${m + 1}/$y"
                    etBirth.setText(fecha)
                    // Después de poner la fecha, vuelvo a llamar al watcher.
                    watcher()
                },
                year,
                month,
                day
            )

            datePicker.show()
        }

        // ---------- OBSERVAR LIVEDATA: HABILITAR BOTÓN ----------
        // Observo el LiveData del ViewModel que me dice si el botón debe estar activo.
        registerViewModel.isRegisterEnabled.observe(viewLifecycleOwner) { enabled ->
            btnCreate.isEnabled = enabled

            // Cambio también los colores para que visualmente se note el estado.
            if (enabled) {
                // Botón activo: morado + texto blanco.
                btnCreate.setBackgroundColor(0xFF7B61FF.toInt())
                btnCreate.setTextColor(0xFFFFFFFF.toInt())
            } else {
                // Botón desactivado: gris clarito.
                btnCreate.setBackgroundColor(0xFFE0E0E0.toInt())
                btnCreate.setTextColor(0xFFAAAAAA.toInt())
            }
        }

        // ---------- BOTÓN "CREAR CUENTA" ----------
        // Aquí es donde realmente proceso el registro cuando el usuario pulsa el botón.
        btnCreate.setOnClickListener {
            // Envuelvo todo en un try/catch por si hubiese algún error inesperado;
            // así evito que la app se cierre y puedo mostrar el mensaje.
            try {
                val user = etUser.text.toString()
                val email = etEmail.text.toString()
                val pass = etPass.text.toString()
                val passConfirm = etPassConfirm.text.toString()
                val birth = etBirth.text.toString()

                // Primero reviso las contraseñas por si acaso.
                if (!registerViewModel.passwordsMatch(pass, passConfirm)) {
                    Snackbar.make(
                        view,
                        getString(R.string.error_passwords_not_match),
                        Snackbar.LENGTH_SHORT
                    ).show()

                    binding.etPassReg.error="Error: las contraseñas no coinciden"

                } else {
                    // En esta tarea NO guardo el usuario en ninguna base de datos.
                    // Simplemente notifico que el registro ha ido bien.
                    Snackbar.make(
                        view,
                        getString(R.string.register_success),
                        Snackbar.LENGTH_SHORT
                    ).show()

                    // Y vuelvo a la pantalla de Login usando la acción del nav_graph.
                    findNavController().navigate(
                        R.id.action_registerFragment_to_loginFragment
                    )
                }
            } catch (e: Exception) {
                // Si se produjera algún error, en vez de petar la app, lo muestro aquí.
                Snackbar.make(
                    requireView(),
                    "Error en registro: ${e.message}",
                    Snackbar.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpio el binding cuando la vista se destruye para evitar fugas de memoria.
        _binding = null
    }
}
