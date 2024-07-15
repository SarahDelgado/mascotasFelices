package com.iespadresuarez.mascotasfelices.fragmentosEmpleado

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
import com.iespadresuarez.mascotasfelices.Login
import com.iespadresuarez.mascotasfelices.PaseoAdapter
import com.iespadresuarez.mascotasfelices.R
import com.iespadresuarez.mascotasfelices.modelosBd.Paseo
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DetallesTrabajoPaseador : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerViewPaseos: RecyclerView
    private lateinit var paseosAdapter: PaseoAdapter

    private lateinit var txtFecha: TextView
    private lateinit var txtLibre: TextView

    private lateinit var fechaFormateadaEnviar: String

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
        val view = inflater.inflate(R.layout.fragment_detalles_trabajo_paseador, container, false)

        txtFecha = view.findViewById(R.id.txtFecha)
        txtLibre = view.findViewById(R.id.txtLibre)
        recyclerViewPaseos = view.findViewById(R.id.recyclerViewPaseos)
        recyclerViewPaseos.layoutManager = LinearLayoutManager(context)

        // Obtiene la fecha seleccionada del bundle
        val fechaSeleccionada = arguments?.getLong("selectedDate") ?: 0L
        val fecha = Date(fechaSeleccionada)

        val fechaFormateada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fecha)
        txtFecha.text = fechaFormateada

        fechaFormateadaEnviar = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(fecha)

        // Llama a obtenerListaPaseos con una devolución de llamada para configurar el adaptador
        obtenerListaPaseos { listaPaseos ->
            paseosAdapter = PaseoAdapter(listaPaseos)
            recyclerViewPaseos.adapter = paseosAdapter
        }
        return view
    }

    /**
     * Método para obtener la lista de paseos
     */
    private fun obtenerListaPaseos(callback: (List<Paseo>) -> Unit) {

        val paseador = Login.empleado?.dni

        val requestQueue = Volley.newRequestQueue(requireContext())
        val listaPaseosUrl =
            "http://${Login.ip}/mascotasfelices/listarpaseospaseador.php?paseador=$paseador&fecha=$fechaFormateadaEnviar"
        val listaPaseosRequest = JsonArrayRequest(
            Request.Method.GET, listaPaseosUrl, null,
            { response ->
                val listaPaseos = mutableListOf<Paseo>()
                for (i in 0 until response.length()) {
                    val paseoObject = response.getJSONObject(i)
                    val paseo = Paseo(
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
                txtLibre.visibility = View.VISIBLE
            }
        )
        requestQueue.add(listaPaseosRequest)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetallesTrabajoPaseador().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}