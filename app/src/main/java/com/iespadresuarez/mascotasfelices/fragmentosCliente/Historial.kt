package com.iespadresuarez.mascotasfelices.fragmentosCliente

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.iespadresuarez.mascotasfelices.HospedajeAdapter
import com.iespadresuarez.mascotasfelices.Login
import com.iespadresuarez.mascotasfelices.PaseoAdapter
import com.iespadresuarez.mascotasfelices.R
import com.iespadresuarez.mascotasfelices.modelosBd.Paseo
import com.iespadresuarez.mascotasfelices.modelosBd.Hospedaje
import java.time.LocalDate
import java.time.LocalTime

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class Historial : Fragment() {

    private lateinit var recyclerViewPaseos: RecyclerView
    private lateinit var recyclerViewHospedajes: RecyclerView
    private lateinit var paseosAdapter: PaseoAdapter
    private lateinit var hospedajesAdapter: HospedajeAdapter

    private lateinit var txtNoPaseos: TextView
    private lateinit var txtNoHospedajes: TextView

    private var param1: String? = null
    private var param2: String? = null

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
        val rootView = inflater.inflate(R.layout.fragment_historial, container, false)

        txtNoPaseos = rootView.findViewById(R.id.txtNoPaseos)
        txtNoHospedajes = rootView.findViewById(R.id.txtNoHospedajes)

        recyclerViewPaseos = rootView.findViewById(R.id.recyclerViewPaseos)
        recyclerViewPaseos.layoutManager = LinearLayoutManager(context)

        // Llama a obtenerListaPaseos con una devolución de llamada para configurar el adaptador
        obtenerListaPaseos { listaPaseos ->
            paseosAdapter = PaseoAdapter(listaPaseos)
            recyclerViewPaseos.adapter = paseosAdapter
        }

        recyclerViewHospedajes = rootView.findViewById(R.id.recyclerViewHospedajes)
        recyclerViewHospedajes.layoutManager = LinearLayoutManager(context)

        // Llama a obtenerListaEmpleados con una devolución de llamada para configurar el adaptador
        obtenerListaHospedajes { listaHospedajes ->
            hospedajesAdapter = HospedajeAdapter(listaHospedajes)
            recyclerViewPaseos.adapter = hospedajesAdapter
        }

        return rootView
    }

    /**
     * Método para obtener la lista de paseos
     */
    private fun obtenerListaPaseos(callback: (List<Paseo>) -> Unit) {
        val codCliente = Login.cliente?.codCliente

        val requestQueue = Volley.newRequestQueue(requireContext())
        val listaPaseosUrl =
            "http://${Login.ip}/mascotasfelices/listarpaseoscliente.php?codCliente=$codCliente"
        val listaPaseosRequest = JsonArrayRequest(
            Request.Method.GET, listaPaseosUrl, null,
            { response ->
                val listaPaseos = mutableListOf<Paseo>()
                for (i in 0 until response.length()) {
                    val paseoObject = response.getJSONObject(i)
                    val paseo = Paseo(
                        paseador = paseoObject.getString("nombrePaseador"),
                        mascota = paseoObject.getString("nombreMascota"),
                        fecha = LocalDate.parse(paseoObject.getString("fecha")),
                        horaInicio = LocalTime.parse(paseoObject.getString("horaInicio")),
                        horaFin = LocalTime.parse(paseoObject.getString("horaFin"))
                    )
                    listaPaseos.add(paseo)
                }
                callback(listaPaseos)
            },
            { _ ->
                txtNoPaseos.visibility = View.VISIBLE
            }
        )
        requestQueue.add(listaPaseosRequest)
    }

    // Método para obtener la lista de hospedajes
    private fun obtenerListaHospedajes(callback: (List<Hospedaje>) -> Unit) {

        val requestQueue = Volley.newRequestQueue(requireContext())
        val codCliente = Login.cliente?.codCliente

        val listaHospedajesUrl =
            "http://${Login.ip}/mascotasfelices/listarhospedajescliente.php?codCliente=$codCliente"
        val listaHospedajesRequest = JsonArrayRequest(
            Request.Method.GET, listaHospedajesUrl, null,
            { response ->
                val listaHospedajes = mutableListOf<Hospedaje>()
                for (i in 0 until response.length()) {
                    val hospedajeObject = response.getJSONObject(i)
                    val hospedaje = Hospedaje(
                        nombreResidencia = hospedajeObject.getString("nombreResidencia"),
                        numHabitacion = hospedajeObject.getInt("numHabitacion"),
                        nombreMascota = hospedajeObject.getString("nombreMascota"),
                        fechaInicio = LocalDate.parse(hospedajeObject.getString("fechaInicio")),
                        fechaFin = LocalDate.parse(hospedajeObject.getString("fechaFin")),
                        nombreHabitacion = hospedajeObject.getString("nombreHabitacion")
                    )
                    listaHospedajes.add(hospedaje)
                }
                callback(listaHospedajes)
            },
            { _ ->
                txtNoHospedajes.visibility = View.VISIBLE
            }
        )
        requestQueue.add(listaHospedajesRequest)
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Historial().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}