package com.iespadresuarez.mascotasfelices

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iespadresuarez.mascotasfelices.modelosBd.Paseo

class PaseoAdapter(private val listaPaseos: List<Paseo>) : RecyclerView.Adapter<PaseoAdapter.PaseoViewHolder>() {

    inner class PaseoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombreMascota: TextView = itemView.findViewById(R.id.txtNombreMascota)
        val txtNombrePaseador: TextView = itemView.findViewById(R.id.txtNombrePaseador)
        val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
        val txtHoraInicio: TextView = itemView.findViewById(R.id.txtHoraInicio)
        val txtHoraFin: TextView = itemView.findViewById(R.id.txtHoraFin)
        val txtCodResidencia: TextView = itemView.findViewById(R.id.txtCodResidencia)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaseoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_paseo, parent, false)
        return PaseoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PaseoViewHolder, position: Int) {
        val paseo = listaPaseos[position]
        holder.txtNombreMascota.text = paseo.mascota
        holder.txtNombrePaseador.text = paseo.paseador
        holder.txtFecha.text = paseo.fecha.toString()
        holder.txtHoraInicio.text = paseo.horaInicio.toString()
        holder.txtHoraFin.text = paseo.horaFin.toString()
        holder.txtCodResidencia.text = paseo.codResidencia.toString()
    }

    override fun getItemCount(): Int {
        return listaPaseos.size
    }

}
