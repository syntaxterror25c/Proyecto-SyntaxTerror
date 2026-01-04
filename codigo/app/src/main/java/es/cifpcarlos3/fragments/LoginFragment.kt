package es.cifpcarlos3.fragments

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
import es.cifpcarlos3.databinding.FragmentLoginBinding
import es.cifpcarlos3.viewmodels.LoginViewModel

// Este fragment representa la pantalla de LOGIN de la tarea.
// Aquí es donde el usuario introduce su usuario y contraseña
// y, si el login es correcto, lo llevo a la pantalla principal con pestañas (TabFragment).
class LoginFragment : Fragment() {

    // ---------------- VIEWBINDING ----------------
    // Uso ViewBinding para acceder a las vistas del layout fragment_login.xml
    // En vez de usar findViewById, que es más engorroso, el binding me da acceso
    // directo a todas las vistas con su id.
    private var _binding: FragmentLoginBinding? = null   // esta referencia puede ser null

    // binding nunca es null mientras la vista del fragment está viva.
    // Por eso uso esta propiedad solo entre onCreateView y onDestroyView.
    private val binding get() = _binding!!

    // ---------------- VIEWMODEL ----------------
    // ViewModel que contiene la lógica de validación del login.
    // Lo creo con el delegado by viewModels(), que lo asocia al ciclo de vida del fragment.
    private val loginViewModel: LoginViewModel by viewModels()

    // onCreateView: aquí inflo el layout del fragment usando el binding.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle? //importante guarda datos para poder restaurar la vista
    ): View {
        // Inflo el layout de login a través de FragmentLoginBinding.
        // Esto genera un objeto binding que tiene todas las vistas del XML.
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        // Devuelvo la raíz del layout para que Android la muestre en pantalla.
        return binding.root
    }

    // onViewCreated: la vista ya está creada y aquí configuro toda la lógica:
    // listeners de botones, observadores del ViewModel, etc.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ---------------- REFERENCIAS A LAS VISTAS ----------------
        // Gracias al binding, accedo a los elementos del XML por su id
        // sin necesidad de usar findViewById.
        val etUser = binding.etUser            // campo de texto para el usuario
        val etPassword = binding.etPassword    // campo de texto para la contraseña
        val btnLogin = binding.btnLogin        // botón de iniciar sesión normal
        val btnLoginGmail = binding.btnLoginGmail  // botón de "Login con Google" (simulado)
        val tvGoToRegister = binding.tvGoToRegister // texto clicable para ir a registro

        // ---------------- TEXTWATCHER ----------------HACE CAMBIOS MIENTRAS ESCRIBO
        // Quiero que el botón de login solo se active cuando haya texto
        // en los campos que yo considere (usuario y contraseña).
        // Para eso, cada vez que cambie algo en los campos, llamo a onFieldsChanged
        // del ViewModel, y él decide si el botón se habilita o no.
        val watcher = {
            // Obtengo los textos actuales de usuario y contraseña.
            val userText = etUser.text.toString()
            val passText = etPassword.text.toString()

            // Aviso al ViewModel pasándole esos dos valores.
            loginViewModel.onFieldsChanged(userText, passText)
        }

        // Cuando el usuario escribe o borra texto en el campo de usuario,
        // llamo al watcher para que el ViewModel vuelva a validar.
        etUser.addTextChangedListener { watcher() }

        // Igual con el campo de contraseña.
        etPassword.addTextChangedListener { watcher() }

        // ---------------- OBSERVAR LIVEDATA (HABILITAR / DESHABILITAR BOTÓN) ----------------
        // Observo el LiveData isLoginEnabled del ViewModel.
        // Este LiveData me dice si el botón de login debe estar activo o no.
        loginViewModel.isLoginEnabled.observe(viewLifecycleOwner) { enabled ->
            // Activo o desactivo el botón según el valor del LiveData.
            btnLogin.isEnabled = enabled

            // Además de la lógica, cambio los colores para que visualmente
            // se note cuando el botón está "activo" o "desactivado".
            if (enabled) {
                // Botón activo: fondo morado y texto blanco.
                btnLogin.setBackgroundColor(0xFF7B61FF.toInt())
                btnLogin.setTextColor(0xFFFFFFFF.toInt())
            } else {
                // Botón inactivo: fondo gris y texto gris
                btnLogin.setBackgroundColor(0xFFE0E0E0.toInt())
                btnLogin.setTextColor(0xFFAAAAAA.toInt())
            }
        }

        // ---------------- BOTÓN LOGIN NORMAL ----------------
        // Aquí implemento la lógica cuando el usuario pulsa el botón de "Iniciar sesión".
        btnLogin.setOnClickListener {
            // Primero recojo lo que el usuario ha escrito en los EditText.
            val user = etUser.text.toString()
            val pass = etPassword.text.toString()

            // Uso el ViewModel para comprobar si el login es correcto.
            // La función isValidLogin encapsula la lógica de validación,
            // así en el fragment solo me preocupo de la parte de la interfaz.
            if (loginViewModel.isValidLogin(user, pass)) {
                // Si el login es válido, muestro un mensaje de éxito con un Snackbar.
                Snackbar.make(
                    view,
                    getString(R.string.login_correct),
                    Snackbar.LENGTH_SHORT
                ).show()

                // IMPORTANTE PARA LA TAREA 2:
                // Después de un login correcto, navego a la pantalla principal
                // que en mi caso es el TabFragment. En ese fragment es donde
                // tengo montados el TabLayout + ViewPager2 con las pestañas
                // de Lista y Favoritos.
                //
                // Para navegar utilizo el Navigation Component y esta acción
                // definida en el nav_graph.xml: action_loginFragment_to_tabFragment.aquiiiiii
                findNavController().navigate(
                    R.id.action_loginFragment_to_tabFragment
                )
            } else {
                // Si el login NO es válido, muestro un mensaje de error.
                // Aquí no navego a ningún sitio, simplemente aviso al usuario.
                Snackbar.make(
                    view,
                    getString(R.string.login_incorrect),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        // ---------------- BOTÓN LOGIN CON GOOGLE (SIMULADO) ----------------
        // En esta tarea no implemento la autenticación real con Google.
        // Solo muestro un mensaje indicando que todavía no está disponible.
        // Esto sirve para cumplir el requisito de la interfaz sin meternos
        // en la complejidad de OAuth, etc.
        btnLoginGmail.setOnClickListener {
            Snackbar.make(
                view,
                getString(R.string.google_not_implemented),
                Snackbar.LENGTH_SHORT
            ).show()
        }

        // ---------------- TEXTO LINKABLE: IR A REGISTER ----------------
        // Este texto funciona como un "enlace" (link) que lleva al usuario
        // a la pantalla de registro (RegisterFragment).
        // Uso el Navigation Component para hacer la navegación entre fragments.
        tvGoToRegister.setOnClickListener {
            // Esta acción está definida en el nav_graph.xml
            // y conecta el loginFragment con el registerFragment.
            findNavController().navigate(
                R.id.action_loginFragment_to_registerFragment
            )
        }
    }

    // onDestroyView: cuando la vista del fragment se destruye (por ejemplo,
    // al navegar a otro fragment), limpio el binding poniéndolo a null.
    // Esto se hace para evitar fugas de memoria (memory leaks), ya que la
    // vista deja de existir pero el fragment puede seguir vivo en memoria.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
