package com.iespadresuarez.mascotasfelices.modelosBd

import java.time.LocalDate

class Mascota(
    var codMascota: Int = 0,
    var nombre: String = "",
    var fechNac: LocalDate,
    var tipoMascota: String = "",
    var raza: String = "",
    var esterilizado: Int = 0,
    var peso: Float = 0.0f,
    var propietario: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Mascota

        return codMascota == other.codMascota
    }

    override fun hashCode(): Int {
        return codMascota
    }
}