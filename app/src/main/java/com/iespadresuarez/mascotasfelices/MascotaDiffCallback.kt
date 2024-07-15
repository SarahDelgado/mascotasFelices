package com.iespadresuarez.mascotasfelices

import androidx.recyclerview.widget.DiffUtil
import com.iespadresuarez.mascotasfelices.modelosBd.Mascota

class MascotaDiffCallback : DiffUtil.ItemCallback<Mascota>() {
    override fun areItemsTheSame(oldItem: Mascota, newItem: Mascota): Boolean {
        return oldItem.codMascota == newItem.codMascota // Usa un identificador único para la comparación
    }

    override fun areContentsTheSame(oldItem: Mascota, newItem: Mascota): Boolean {
        return oldItem == newItem // Compara el contenido real
    }
}