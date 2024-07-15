package com.iespadresuarez.mascotasfelices

import androidx.recyclerview.widget.DiffUtil
import com.iespadresuarez.mascotasfelices.modelosBd.Empleado

class EmpleadoDiffCallback : DiffUtil.ItemCallback<Empleado>() {
    override fun areItemsTheSame(oldItem: Empleado, newItem: Empleado): Boolean {
        return oldItem.dni == newItem.dni // Usa un identificador único para la comparación
    }

    override fun areContentsTheSame(oldItem: Empleado, newItem: Empleado): Boolean {
        return oldItem == newItem // Compara el contenido real
    }
}