package com.iespadresuarez.mascotasfelices.modelosBd

import java.time.LocalDate

class Hospedaje(
    val nombreResidencia: String = "",
    val numHabitacion: Int = 0,
    val nombreMascota: String = "",
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate,
    val nombreHabitacion: String = ""
)
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Hospedaje

        if (nombreResidencia != other.nombreResidencia) return false
        if (numHabitacion != other.numHabitacion) return false
        if (nombreMascota != other.nombreMascota) return false
        if (fechaInicio != other.fechaInicio) return false
        if (fechaFin != other.fechaFin) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nombreResidencia.hashCode()
        result = 31 * result + numHabitacion
        result = 31 * result + nombreMascota.hashCode()
        result = 31 * result + fechaInicio.hashCode()
        result = 31 * result + fechaFin.hashCode()
        result = 31 * result + nombreHabitacion.hashCode()
        return result
    }
}