package com.cifpcarlos3.tarea1y2           // <-- ESTE ES EL BUENO
// ← ajusta si tu paquete es distinto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cifpcarlos3.tarea1y2.R

// Actividad principal de la tarea.
// Solo se encarga de inflar el layout activity_main.xml,
// que contiene el NavHostFragment con Login y Register.
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main) //-----IMPORTANTE----- pone el layout en pantalla
    }
}
