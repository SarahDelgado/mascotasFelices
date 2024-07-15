package com.iespadresuarez.mascotasfelices.fragmentosAdmin

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.iespadresuarez.mascotasfelices.Login
import com.iespadresuarez.mascotasfelices.R
import com.iespadresuarez.mascotasfelices.modelosBd.Habitacion
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddHorario : Fragment() {

    private lateinit var edtxtEmpleado: TextInputEditText
    private lateinit var txtHabitacion: TextInputLayout
    private lateinit var autoCompleteHabitacion: MaterialAutoCompleteTextView
    private lateinit var txtFecha: TextInputLayout
    private lateinit var edtxtFecha: TextInputEditText
    private lateinit var edtxtHoraInicio: TextInputEditText
    private lateinit var txtHoraInicio: TextInputLayout
    private lateinit var edtxtHoraFin: TextInputEditText
    private lateinit var txtHoraFin: TextInputLayout
    private lateinit var btnGuardar: Button

    private var fechaInicio: Long? = null
    private var fechaFin: Long? = null
    private var horaInicio: String? = null
    private var horaFin: String? = null
    private lateinit var dni: String
    private lateinit var nombre: String
    private lateinit var tipoEmpleado: String
    private lateinit var numHabitacion: String
    private var habitacionMap = mutableMapOf<String, Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_horario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edtxtEmpleado = view.findViewById(R.id.edtxtEmpleado)
        txtHabitacion = view.findViewById(R.id.txtHabitacion)
        autoCompleteHabitacion = view.findViewById(R.id.autoCompleteHabitacion)
        txtFecha = view.findViewById(R.id.txtFecha)
        edtxtFecha = view.findViewById(R.id.edtxtFecha)
        edtxtHoraInicio = view.findViewById(R.id.edtxtHoraInicio)
        txtHoraInicio = view.findViewById(R.id.txtHoraInicio)
        edtxtHoraFin = view.findViewById(R.id.edtxtHoraFin)
        txtHoraFin = view.findViewById(R.id.txtHoraFin)
        btnGuardar = view.findViewById(R.id.btnGuardar)

        autoCompleteHabitacion.addTextChangedListener(crearTextWatcher(txtHabitacion))
        edtxtFecha.addTextChangedListener(crearTextWatcher(txtFecha))
        edtxtHoraFin.addTextChangedListener(crearTextWatcher(txtHoraFin))
        edtxtHoraInicio.addTextChangedListener(crearTextWatcher(txtHoraInicio))

        dni = arguments?.getString(ARG_DNI).toString()
        nombre = arguments?.getString(ARG_NOMBRE).toString()
        tipoEmpleado = arguments?.getString(ARG_TIPO).toString()

        edtxtEmpleado.setText(nombre)

        if (tipoEmpleado == "Cuidador") {
            txtHabitacion.visibility = View.VISIBLE
            obtenerHabitaciones { habitaciones ->
                // Llena el mapa y la lista con las habitaciones
                habitaciones.forEach { habitacion ->
                    habitacionMap[habitacion.nombre] = habitacion.numHabitacion
                }
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    habitaciones
                )
                autoCompleteHabitacion.setAdapter(adapter)

                // Opcional: Manejar la selección de una habitación
                autoCompleteHabitacion.setOnItemClickListener { _, _, position, _ ->
                    val habitacionSeleccionada = habitaciones[position]
                    autoCompleteHabitacion.setText(habitacionSeleccionada.toString())
                    numHabitacion = habitacionSeleccionada.numHabitacion.toString()
                }
            }
        } else if (tipoEmpleado == "Paseador") {
            txtHabitacion.visibility = View.GONE
        }

        btnGuardar.setOnClickListener {
            if (tipoEmpleado == "Cuidador") {
                guardarAgendaCuidador()
            } else if (tipoEmpleado == "Paseador") {
                guardarAgendaPaseador()
            }
        }

        establecerDateRangePicker()
        establecerTimePickers()

    }

    private fun establecerDateRangePicker() {
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Seleccionar rango de fechas")
            .build()

        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Listener para mostrar el DateRangePicker
        val fechaClickListener = View.OnClickListener {
            dateRangePicker.show(parentFragmentManager, "dateRangePicker")
        }

        // Asigna el listener al campo de texto y al icono de finalización
        edtxtFecha.setOnClickListener(fechaClickListener)
        txtFecha.setEndIconOnClickListener(fechaClickListener)

        // Listener para manejar la selección de fechas
        dateRangePicker.addOnPositiveButtonClickListener {
            fechaInicio = it.first
            fechaFin = it.second
            val startDate = formato.format(fechaInicio)
            val endDate = formato.format(fechaFin)
            edtxtFecha.setText(getString(R.string.formatoRangoFechas, startDate, endDate))
        }
    }

    private fun establecerTimePickers() {
        // Configuración para la hora de inicio
        val horaInicioListener = View.OnClickListener {
            mostrarTimePicker { hora, minuto ->
                horaInicio = String.format(Locale.getDefault(), "%02d:%02d", hora, minuto)
                edtxtHoraInicio.setText(horaInicio)
            }
        }
        edtxtHoraInicio.setOnClickListener(horaInicioListener)
        txtHoraInicio.setEndIconOnClickListener(horaInicioListener)

        // Configuración para la hora de fin
        val horaFinListener = View.OnClickListener {
            mostrarTimePicker { hora, minuto ->
                horaFin = String.format(Locale.getDefault(), "%02d:%02d", hora, minuto)
                edtxtHoraFin.setText(horaFin)
            }
        }
        edtxtHoraFin.setOnClickListener(horaFinListener)
        txtHoraFin.setEndIconOnClickListener(horaFinListener)
    }

    private fun mostrarTimePicker(onTimeSet: (hora: Int, minuto: Int) -> Unit) {
        val calendario = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hora, minuto ->
                onTimeSet(hora, minuto)
            },
            calendario.get(Calendar.HOUR_OF_DAY),
            calendario.get(Calendar.MINUTE),
            true
        ).show()
    }

    /**
     * Método para obtener la lista de habitaciones
     */
    private fun obtenerHabitaciones(callback: (List<Habitacion>) -> Unit) {

        val requestQueue = Volley.newRequestQueue(requireContext())
        val residencia = Login.empleado?.residencia

        val listaHabitacionesUrl =
            "http://${Login.ip}/mascotasfelices/listarhabitaciones.php?residencia=$residencia"
        val listaHabitacionesRequest = JsonArrayRequest(
            Request.Method.GET, listaHabitacionesUrl, null,
            { response ->
                val listaHabitaciones = mutableListOf<Habitacion>()
                for (i in 0 until response.length()) {
                    val habitacionObject = response.getJSONObject(i)
                    val habitacion = Habitacion(
                        codResidencia = habitacionObject.getInt("codResidencia"),
                        numHabitacion = habitacionObject.getInt("numHabitacion"),
                        nombre = habitacionObject.getString("nombre")
                    )
                    listaHabitaciones.add(habitacion)
                }
                callback(listaHabitaciones)
            },
            { _ ->
                Toast.makeText(
                    requireContext(),
                    "No ha sido posible recoger las habitaciones",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
        requestQueue.add(listaHabitacionesRequest)
    }

    private fun guardarAgendaCuidador() {

        if (validarCampos()) {

            val url = "http://${Login.ip}/mascotasfelices/insertaragendacuidador.php"
            val requestQueue: RequestQueue = Volley.newRequestQueue(context)
            val resultadoPost = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    if (response.trim() == "success") {
                        Toast.makeText(
                            requireContext(),
                            "Horario añadido con éxito",
                            Toast.LENGTH_LONG
                        ).show()
                        limpiarCampos()
                    } else if (response.trim() == "error") {
                        Toast.makeText(
                            context,
                            "No se ha podido añadir el horario",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }, Response.ErrorListener { _ ->
                    Toast.makeText(
                        context,
                        "No se ha podido añadir el horario",
                        Toast.LENGTH_LONG
                    ).show()
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val parametros = HashMap<String, String>()
                    val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val fechaInicioStr = formato.format(Date(fechaInicio ?: 0))
                    val fechaFinStr = formato.format(Date(fechaFin ?: 0))
                    parametros["cuidador"] = dni
                    parametros["codResidencia"] = Login.empleado?.residencia.toString()
                    parametros["numHabitacion"] = numHabitacion
                    parametros["fechaInicio"] = fechaInicioStr
                    parametros["fechaFin"] = fechaFinStr
                    parametros["horaInicio"] = horaInicio ?: ""
                    parametros["horaFin"] = horaFin ?: ""
                    return parametros
                }
            }
            requestQueue.add(resultadoPost)
        }
    }

    private fun guardarAgendaPaseador() {

        if (validarCampos()) {

            val url = "http://${Login.ip}/mascotasfelices/insertaragendapaseador.php"
            val requestQueue: RequestQueue = Volley.newRequestQueue(context)
            val resultadoPost = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    if (response.trim() == "success") {
                        Toast.makeText(
                            requireContext(),
                            "Horario añadido con éxito",
                            Toast.LENGTH_LONG
                        ).show()
                        limpiarCampos()
                    } else if (response.trim() == "error") {
                        Toast.makeText(
                            context,
                            "No se ha podido añadir el horario",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }, Response.ErrorListener { _ ->
                    Toast.makeText(
                        context,
                        "No se ha podido añadir el horario",
                        Toast.LENGTH_LONG
                    ).show()
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val parametros = HashMap<String, String>()
                    val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val fechaInicioStr = formato.format(Date(fechaInicio ?: 0))
                    val fechaFinStr = formato.format(Date(fechaFin ?: 0))
                    parametros["empleado"] = dni
                    parametros["fechaInicio"] = fechaInicioStr
                    parametros["fechaFin"] = fechaFinStr
                    parametros["horaInicio"] = horaInicio ?: ""
                    parametros["horaFin"] = horaFin ?: ""
                    return parametros
                }
            }
            requestQueue.add(resultadoPost)
        }
    }

    private fun limpiarCampos() {
        autoCompleteHabitacion.setText("")
        edtxtFecha.setText("")
        edtxtHoraInicio.setText("")
        edtxtHoraFin.setText("")
    }

    private fun validarCampos(): Boolean {
        var esValido = true

        if (tipoEmpleado == "Cuidador") {
            if (autoCompleteHabitacion.text.isNullOrEmpty()) {
                txtHabitacion.error = "Selecciona una habitación"
                esValido = false
            } else {
                txtHabitacion.error = null
            }
        }

        if (edtxtFecha.text.isNullOrEmpty()) {
            txtFecha.error = "Selecciona una fecha"
            esValido = false
        } else {
            txtFecha.error = null
        }

        if (edtxtHoraInicio.text.isNullOrEmpty()) {
            txtHoraInicio.error = "Selecciona una hora de inicio"
            esValido = false
        } else {
            txtHoraInicio.error = null
        }

        if (edtxtHoraFin.text.isNullOrEmpty()) {
            txtHoraFin.error = "Selecciona una hora de finalizacion"
            esValido = false
        } else {
            txtHoraFin.error = null
        }

        return esValido
    }

    private fun crearTextWatcher(textInputLayout: TextInputLayout): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textInputLayout.error = null
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    companion object {
        private const val ARG_DNI = "dni"
        private const val ARG_NOMBRE = "nombre"
        private const val ARG_TIPO = "tipoEmpleado"

        @JvmStatic
        fun newInstance(dni: String, nombre: String, tipoEmpleado: String) =
            AddHorario().apply {
                arguments = Bundle().apply {
                    putString(ARG_DNI, dni)
                    putString(ARG_NOMBRE, nombre)
                    putString(ARG_TIPO, tipoEmpleado)
                }
            }
    }

}
