package com.iespadresuarez.mascotasfelices.modelosBd

open class Empleado(
    var dni: String = "",
    var nombre: String = "",
    var direccion: String = "",
    var email: String = "",
    var telefono: String = "",
    var tipoEmpleado: String = "",
    var nivelAfectuoso: Int = 0,
    var nivelAgresivo: Int = 0,
    var nivelEstricto: Int = 0,
    var nivelEnfermos: Int = 0,
    var residencia: Int = 0,
    var salario: Double = 0.0,
    var pagoHora: Float = 0.0f
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Empleado

        return dni == other.dni
    }

    override fun hashCode(): Int {
        return dni.hashCode()
    }

}