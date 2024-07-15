package com.iespadresuarez.mascotasfelices.modelosBd

import java.time.LocalDate
import java.time.LocalTime

class Paseo(
    val paseador: String = "",
    val mascota: String = "",
    val fecha: LocalDate,
    val horaInicio: LocalTime,
    val horaFin: LocalTime,
    val codResidencia: Int = 0
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Paseo

        if (paseador != other.paseador) return false
        if (mascota != other.mascota) return false
        if (fecha != other.fecha) return false
        if (horaInicio != other.horaInicio) return false

        return true
    }

    override fun hashCode(): Int {
        var result = paseador.hashCode()
        result = 31 * result + mascota.hashCode()
        result = 31 * result + fecha.hashCode()
        result = 31 * result + horaInicio.hashCode()
        return result
    }

}