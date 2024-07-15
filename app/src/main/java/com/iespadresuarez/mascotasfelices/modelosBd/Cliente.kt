package com.iespadresuarez.mascotasfelices.modelosBd

class Cliente(
    var codCliente: Int = 0,
    var email: String = "",
    var nombre: String = "",
    var direccion: String = "",
    var telefono1: String = "",
    var telefono2: String = ""
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Cliente

        return codCliente == other.codCliente
    }

    override fun hashCode(): Int {
        return codCliente
    }

}