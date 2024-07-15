package com.iespadresuarez.mascotasfelices

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iespadresuarez.mascotasfelices.modelosBd.Empleado

class PaseadorAdapter(private val paseadores: List<Empleado>) : RecyclerView.Adapter<PaseadorAdapter.PaseadorViewHolder>() {

    class PaseadorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombrePaseador)
        val txtNivelAfectuoso: TextView = itemView.findViewById(R.id.txtNivelAfectuoso)
        val txtNivelAgresivo: TextView = itemView.findViewById(R.id.txtNivelAgresivo)
        val txtNivelEnfermos: TextView = itemView.findViewById(R.id.txtNivelEnfermos)
        val txtNivelEstricto: TextView = itemView.findViewById(R.id.txtNivelEstricto)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaseadorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_paseador, parent, false)
        return PaseadorViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaseadorViewHolder, position: Int) {
        val paseador = paseadores[position]
        holder.txtNombre.text = paseador.nombre
        holder.txtNivelAfectuoso.text = paseador.nivelAfectuoso.toString()
        holder.txtNivelAgresivo.text = paseador.nivelAgresivo.toString()
        holder.txtNivelEnfermos.text = paseador.nivelEnfermos.toString()
        holder.txtNivelEstricto.text = paseador.nivelEstricto.toString()
    }

    override fun getItemCount(): Int {
        return paseadores.size
    }
}