package es.cifpcarlos3

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


/**
 * Pantalla de SPLASH de la Tarea.
 *
 * Versión simplificada: en cuanto se crea, salta directamente a MainActivity.
 * Si con esto ya se cierra la app, el fallo NO está en el Handler ni en el delay,
 * sino en el arranque de MainActivity o en el tema.
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pinto el layout del splash (logo + título).
        setContentView(R.layout.activity_splash)

        // Lanzo directamente la MainActivity.
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        // Cierro el splash para que no vuelva al darle atrás.
        finish()
    }
}
