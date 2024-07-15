package com.iespadresuarez.mascotasfelices

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.iespadresuarez.mascotasfelices.modelosBd.Empleado

class EmpleadoAdapter(
    private val onHorarioClick: (Empleado) -> Unit
) :
    ListAdapter<Empleado, EmpleadoAdapter.EmpleadoViewHolder>(EmpleadoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpleadoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_empleado, parent, false)
        return EmpleadoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EmpleadoViewHolder, position: Int) {
        val empleado = getItem(position)
        holder.bind(empleado)
    }

    inner class EmpleadoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtNombre: TextView = itemView.findViewById(R.id.txtNombre)
        private val txtTipoEmpleado: TextView = itemView.findViewById(R.id.txtTipoEmpleado)
        private val txtEmail: TextView = itemView.findViewById(R.id.txtEmail)
        private val txtTelefono: TextView = itemView.findViewById(R.id.txtTelefono)
        private val fabAddHorario: FloatingActionButton = itemView.findViewById(R.id.fabAddHorario)

        fun bind(empleado: Empleado) {
            txtNombre.text = empleado.nombre
            txtTipoEmpleado.text = empleado.tipoEmpleado
            txtEmail.text = empleado.email
            txtTelefono.text = empleado.telefono

            // Abre el fragmento de añadir horario al empleado al hacer clic en el FloatingActionButton
            fabAddHorario.setOnClickListener {
                onHorarioClick(empleado)
            }
        }
    }

}
