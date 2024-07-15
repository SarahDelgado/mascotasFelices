package com.iespadresuarez.mascotasfelices

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iespadresuarez.mascotasfelices.modelosBd.Mascota
import java.time.LocalDate
import java.time.Period

class MascotaAdapter(
    private val mascotas: List<Mascota>,
    private val clickListener: OnMascotaClickListener
) :
    ListAdapter<Mascota, MascotaAdapter.MascotaViewHolder>(MascotaDiffCallback()) {

    interface OnMascotaClickListener {
        fun onMascotaClick(mascota: Mascota)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mascota, parent, false)
        return MascotaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MascotaViewHolder, position: Int) {
        val mascota = getItem(position)
        holder.nombre.text = mascota.nombre
        holder.edad.text = "Edad: ${calcularEdad(mascota.fechNac)}"
        holder.peso.text = "Peso: ${mascota.peso} kg"
        holder.raza.text = "Raza: ${mascota.raza}"

        holder.itemView.setOnClickListener {
            clickListener.onMascotaClick(mascota)
        }
    }

    inner class MascotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.txtNombre)
        val edad: TextView = itemView.findViewById(R.id.txtEdad)
        val peso: TextView = itemView.findViewById(R.id.txtPeso)
        val raza: TextView = itemView.findViewById(R.id.txtRaza)

        fun bind(mascota: Mascota) {
            nombre.text = mascota.nombre
            peso.text = mascota.peso.toString()
            raza.text = mascota.raza
        }

    }

    /**
     * Función para calcular la edad en años basada en la fecha de nacimiento
     */
    private fun calcularEdad(fechaNacimiento: LocalDate): Int {
        val fechaActual = LocalDate.now()
        val periodo = Period.between(fechaNacimiento, fechaActual)
        return periodo.years
    }

}

