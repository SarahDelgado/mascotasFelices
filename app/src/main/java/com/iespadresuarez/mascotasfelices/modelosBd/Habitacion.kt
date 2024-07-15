package com.iespadresuarez.mascotasfelices.modelosBd

class Habitacion(
    var codResidencia: Int = 0,
    var numHabitacion: Int = 0,
    var nombre: String = ""
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Habitacion

        if (codResidencia != other.codResidencia) return false
        if (numHabitacion != other.numHabitacion) return false

        return true
    }

    override fun hashCode(): Int {
        var result = codResidencia
        result = 31 * result + numHabitacion
        return result
    }

    override fun toString(): String {
        return "$numHabitacion - $nombre"
    }

}