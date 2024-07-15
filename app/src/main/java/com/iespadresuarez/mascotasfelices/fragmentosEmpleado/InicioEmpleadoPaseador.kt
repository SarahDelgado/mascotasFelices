package com.iespadresuarez.mascotasfelices.fragmentosEmpleado

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import com.iespadresuarez.mascotasfelices.R
import java.util.Calendar

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class InicioEmpleadoPaseador : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var calendarioPaseador: CalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_inicio_empleado_paseador, container, false)

        calendarioPaseador = view.findViewById(R.id.calendarioPaseador)
        // Configura el CalendarView para mostrar el día de hoy
        val calendario = Calendar.getInstance()
        val diaActual = calendario.timeInMillis
        calendarioPaseador.date = diaActual

        // Configura el listener para cambios de fecha
        calendarioPaseador.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val fechaSeleccionada = Calendar.getInstance()
            fechaSeleccionada.set(year, month, dayOfMonth)
            val dateInMillis = fechaSeleccionada.timeInMillis

            // Navega al fragmento de detalles y pasa la fecha seleccionada
            val bundle = Bundle().apply {
                putLong("selectedDate", dateInMillis)
            }

            val detallesTrabajoPaseador = DetallesTrabajoPaseador().apply {
                arguments = bundle
            }

            // Utiliza el FragmentManager de la actividad principal
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.frameLayoutEmpleado, detallesTrabajoPaseador)
                ?.addToBackStack(null)
                ?.commit()
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InicioEmpleadoPaseador().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}