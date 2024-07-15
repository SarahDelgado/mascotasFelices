package com.iespadresuarez.mascotasfelices

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/**
 * Actividad que muestra los términos y condiciones de la aplicación.
 */
class Terminos : AppCompatActivity() {

    /**
     * Método llamado cuando se crea la actividad.
     * @param savedInstanceState Estado previamente guardado de la actividad, si existe.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terminos)
    }

}