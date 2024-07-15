package com.iespadresuarez.mascotasfelices

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iespadresuarez.mascotasfelices.modelosBd.Hospedaje
import java.time.format.DateTimeFormatter

class HospedajeAdapter(private val listaHospedajes: List<Hospedaje>) :
    RecyclerView.Adapter<HospedajeAdapter.HospedajeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospedajeViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hospedaje, parent, false)
        return HospedajeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HospedajeViewHolder, position: Int) {
        val hospedajeActual = listaHospedajes[position]
        holder.textViewNombreResidencia.text = hospedajeActual.nombreResidencia
        holder.textViewNumHabitacion.text = hospedajeActual.numHabitacion.toString()
        holder.textViewNombreHabitacion.text = hospedajeActual.nombreHabitacion
        holder.textViewNombreMascota.text = hospedajeActual.nombreMascota
        holder.textViewFechaInicio.text =
            hospedajeActual.fechaInicio.format(DateTimeFormatter.ISO_LOCAL_DATE)
        holder.textViewFechaFin.text =
            hospedajeActual.fechaFin.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    override fun getItemCount() = listaHospedajes.size

    class HospedajeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNombreResidencia: TextView =
            itemView.findViewById(R.id.textViewNombreResidencia)
        val textViewNumHabitacion: TextView = itemView.findViewById(R.id.textViewNumHabitacion)
        val textViewNombreHabitacion: TextView =
            itemView.findViewById(R.id.textViewNombreHabitacion)
        val textViewNombreMascota: TextView = itemView.findViewById(R.id.textViewNombreMascota)
        val textViewFechaInicio: TextView = itemView.findViewById(R.id.textViewFechaInicio)
        val textViewFechaFin: TextView = itemView.findViewById(R.id.textViewFechaFin)
    }

}