package com.iespadresuarez.mascotasfelices

import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Bundle
import android.content.Intent
import android.os.Looper

/**
 * Actividad que muestra una pantalla de carga antes de iniciar la actividad principal.
 */
class PantallaDeCarga : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_de_carga)

        // Duración de la pantalla de carga en milisegundos (en este caso, 5000 ms = 5 segundos)
        val duracion: Long = 5000

        // Crea un Handler en el hilo principal (main looper) para manejar la espera
        Handler(Looper.getMainLooper()).postDelayed({
            // Después del tiempo de espera, inicia la actividad de login
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            // Cierra la actividad actual
            finish()
        }, duracion)
    }

}