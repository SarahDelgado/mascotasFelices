package com.iespadresuarez.mascotasfelices.fragmentosEmpleado

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.iespadresuarez.mascotasfelices.Login
import com.iespadresuarez.mascotasfelices.MascotaAdapter
import com.iespadresuarez.mascotasfelices.R
import com.iespadresuarez.mascotasfelices.modelosBd.Mascota
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DetallesTrabajoCuidador : Fragment(), MascotaAdapter.OnMascotaClickListener {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var mascotaAdapter: MascotaAdapter

    private lateinit var txtNoMascotas: TextView
    private lateinit var txtFecha: TextView
    private lateinit var txtHorario: TextView
    private lateinit var txtHabitacion: TextView
    private lateinit var txtMascotas: TextView
    private lateinit var txtLibre: TextView

    private lateinit var fechaEnviada: String
    private var codResidencia by Delegates.notNull<Int>()
    private var numHabitacion by Delegates.notNull<Int>()
    private lateinit var nombreHabitacion: String
    private lateinit var horaInicio: LocalTime
    private lateinit var horaFin: LocalTime

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
        val view = inflater.inflate(R.layout.fragment_detalles_trabajo_cuidador, container, false)

        // Inicialización de las vistas
        txtFecha = view.findViewById(R.id.txtFecha)
        txtHorario = view.findViewById(R.id.txtHorario)
        txtHabitacion = view.findViewById(R.id.txtHabitacion)
        txtMascotas = view.findViewById(R.id.txtMascotas)
        txtNoMascotas = view.findViewById(R.id.txtNoMascotas)
        txtLibre = view.findViewById(R.id.txtLibre)
        recyclerView = view.findViewById(R.id.recyclerViewMascotas)

        codResidencia = Login.empleado?.residencia!!

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = mascotaAdapter

        /*
        obtenerListaMascotas { listaMascotas ->
            mascotaAdapter = MascotaAdapter(listaMascotas)
            recyclerView.adapter = mascotaAdapter
        }
        */

        // Obtiene la fecha seleccionada del bundle
        val fechaSeleccionada = arguments?.getLong("selectedDate") ?: 0L
        val fecha = Date(fechaSeleccionada)
        fechaEnviada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(fecha)
        val fechaMostrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fecha)
        txtFecha.text = fechaMostrada

        obtenerAgenda()

        return view
    }

    override fun onMascotaClick(mascota: Mascota) {
        // Maneja el evento de clic aquí
        Toast.makeText(requireContext(), "Clicked on: ${mascota.nombre}", Toast.LENGTH_SHORT).show()
    }

    /**
     * Método para obtener la agenda del cuidador
     */
    private fun obtenerAgenda() {
        val requestQueue = Volley.newRequestQueue(requireContext())
        val agendaUrl = "http://${Login.ip}/mascotasfelices/buscaragendacuidador.php?cuidador=${Login.empleado?.dni}&codResidencia=$codResidencia&fecha=$fechaEnviada"

        val agendaRequest = JsonObjectRequest(
            Request.Method.GET, agendaUrl, null,
            { response ->
                if (response.has("nombre")) {
                    numHabitacion = response.getInt("numHabitacion")
                    nombreHabitacion = response.getString("nombre")
                    val formato = DateTimeFormatter.ofPattern("HH:mm:ss")
                    horaInicio = LocalTime.parse(response.getString("horaInicio"), formato)
                    horaFin = LocalTime.parse(response.getString("horaFin"), formato)
                    actualizarUI()
                } else {
                    mostrarDiaLibre()
                }
            },
            { _ ->
                Toast.makeText(
                    requireContext(),
                    "No se ha podido obtener la agenda de este día",
                    Toast.LENGTH_LONG
                ).show()
                mostrarDiaLibre()
            }
        )
        requestQueue.add(agendaRequest)
    }

    private fun actualizarUI() {
        txtLibre.visibility = View.GONE
        txtHorario.visibility = View.VISIBLE
        txtHabitacion.visibility = View.VISIBLE
        txtMascotas.visibility = View.VISIBLE

        txtHorario.text = getString(R.string.horarioLaboral, horaInicio.toString(), horaFin.toString())
        txtHabitacion.text = getString(R.string.habitacionAsignada, numHabitacion, nombreHabitacion)

        obtenerListaMascotas { listaMascotas ->
            if (listaMascotas.isNotEmpty()) {
                recyclerView.visibility = View.VISIBLE
                txtNoMascotas.visibility = View.GONE
            } else {
                recyclerView.visibility = View.GONE
                txtNoMascotas.visibility = View.VISIBLE
            }
        }
    }

    private fun mostrarDiaLibre() {
        txtHorario.visibility = View.GONE
        txtHabitacion.visibility = View.GONE
        txtMascotas.visibility = View.GONE
        txtLibre.visibility = View.VISIBLE
    }

    /**
     * Método para obtener la lista de mascotas
     */
    private fun obtenerListaMascotas(callback: (List<Mascota>) -> Unit) {
        val requestQueue = Volley.newRequestQueue(requireContext())
        val listaMascotasUrl = "http://${Login.ip}/mascotasfelices/listarmascotashospedaje.php?codResidencia=$codResidencia&numHabitacion=$numHabitacion&fechaHoy=$fechaEnviada"

        val listaMascotasRequest = JsonObjectRequest(
            Request.Method.GET, listaMascotasUrl, null,
            { response ->
                val listaMascotas = mutableListOf<Mascota>()
                if (response.has("mascotas")) {
                    val mascotasArray = response.getJSONArray("mascotas")
                    for (i in 0 until mascotasArray.length()) {
                        val mascotaObject = mascotasArray.getJSONObject(i)
                        val mascota = Mascota(
                            codMascota = mascotaObject.getInt("codMascota"),
                            nombre = mascotaObject.getString("nombre"),
                            fechNac = LocalDate.parse(mascotaObject.getString("fechNac")),
                            tipoMascota = mascotaObject.getString("tipoMascota"),
                            raza = mascotaObject.getString("raza"),
                            esterilizado = mascotaObject.getInt("esterilizado"),
                            peso = mascotaObject.getDouble("peso").toFloat(),
                            propietario = mascotaObject.getInt("propietario")
                        )
                        listaMascotas.add(mascota)
                    }
                }
                callback(listaMascotas)
            },
            { _ ->
                Toast.makeText(
                    requireContext(),
                    "No se han podido obtener las mascotas para este día",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
        requestQueue.add(listaMascotasRequest)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetallesTrabajoCuidador().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
