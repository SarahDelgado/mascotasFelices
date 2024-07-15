package com.iespadresuarez.mascotasfelices.fragmentosCliente

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
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
import com.iespadresuarez.mascotasfelices.modelosBd.Empleado
import com.iespadresuarez.mascotasfelices.modelosBd.Mascota
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale
import kotlin.properties.Delegates

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Paseos : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var btnPagar: Button

    private lateinit var paymentSheet: PaymentSheet
    private lateinit var paymentIntentClientSecret: String
    private lateinit var configuracion: PaymentSheet.CustomerConfiguration

    private lateinit var autocompleteMascota: MaterialAutoCompleteTextView
    private lateinit var edtxtFecha: TextInputEditText
    private lateinit var edtxtHoraInicio: TextInputEditText
    private lateinit var edtxtHoraFin: TextInputEditText
    private lateinit var autocompletePaseador: MaterialAutoCompleteTextView

    private lateinit var txtMascota: TextInputLayout
    private lateinit var txtFecha: TextInputLayout
    private lateinit var txtHoraInicio: TextInputLayout
    private lateinit var txtHoraFin: TextInputLayout
    private lateinit var txtPaseador: TextInputLayout

    private lateinit var txtTotal: TextView

    private lateinit var linearLayoutNivelAfectuoso: LinearLayout
    private lateinit var linearLayoutNivelAgresivo: LinearLayout
    private lateinit var linearLayoutNivelEstricto: LinearLayout
    private lateinit var linearLayoutNivelEnfermos: LinearLayout

    private lateinit var ratingBarNivelAfectuoso: RatingBar
    private lateinit var ratingBarNivelAgresivo: RatingBar
    private lateinit var ratingBarNivelEstricto: RatingBar
    private lateinit var ratingBarNivelEnfermos: RatingBar

    private lateinit var listaMascotas: MutableList<Mascota>
    private lateinit var mascotaAdapter: ArrayAdapter<String>

    private var listaPaseadores: MutableList<Empleado> = mutableListOf()
    private var paseadorAdapter: ArrayAdapter<String>? = null

    private lateinit var dni: String
    private var codResidencia by Delegates.notNull<Int>()
    private var precioTotal by Delegates.notNull<Double>()

    // Propiedades inicializadas con valores predeterminados
    private var fecha: Long = 0
    private var fechaFormateadaEnviar: String = ""
    private var horaInicio: String = ""
    private var horaFin: String = ""
    private var pagoHora: Float = 0.0f

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
        val view = inflater.inflate(R.layout.fragment_paseos, container, false)

        edtxtFecha = view.findViewById(R.id.edtxtFecha)
        autocompleteMascota = view.findViewById(R.id.autocompleteMascota)
        edtxtHoraInicio = view.findViewById(R.id.edtxtHoraInicio)
        edtxtHoraFin = view.findViewById(R.id.edtxtHoraFin)
        autocompletePaseador = view.findViewById(R.id.autocompletePaseador)

        txtFecha = view.findViewById(R.id.txtFecha)
        txtMascota = view.findViewById(R.id.txtMascota)
        txtPaseador = view.findViewById(R.id.txtPaseador)
        txtHoraInicio = view.findViewById(R.id.txtHoraInicio)
        txtHoraFin = view.findViewById(R.id.txtHoraFin)

        txtTotal = view.findViewById(R.id.txtTotal)

        linearLayoutNivelAfectuoso = view.findViewById(R.id.linearLayoutNivelAfectuoso)
        linearLayoutNivelAgresivo = view.findViewById(R.id.linearLayoutNivelAgresivo)
        linearLayoutNivelEstricto = view.findViewById(R.id.linearLayoutNivelEstricto)
        linearLayoutNivelEnfermos = view.findViewById(R.id.linearLayoutNivelEnfermos)

        ratingBarNivelAfectuoso = view.findViewById(R.id.ratingBarNivelAfectuoso)
        ratingBarNivelAgresivo = view.findViewById(R.id.ratingBarNivelAgresivo)
        ratingBarNivelEstricto = view.findViewById(R.id.ratingBarNivelEstricto)
        ratingBarNivelEnfermos = view.findViewById(R.id.ratingBarNivelEnfermos)

        autocompleteMascota.addTextChangedListener(crearTextWatcher(txtMascota))
        edtxtFecha.addTextChangedListener(crearTextWatcher(txtFecha))
        edtxtHoraFin.addTextChangedListener(crearTextWatcher(txtHoraFin))
        edtxtHoraInicio.addTextChangedListener(crearTextWatcher(txtHoraInicio))
        autocompletePaseador.addTextChangedListener(crearTextWatcher(txtPaseador))

        // Obtiene la lista de mascotas
        obtenerMascotas { mascotas ->
            listaMascotas = mascotas.toMutableList()
            val nombresMascotas = mascotas.map { it.nombre }
            mascotaAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                nombresMascotas
            )
            autocompleteMascota.setAdapter(mascotaAdapter)
        }

        autocompletePaseador.setOnItemClickListener { _, _, position, _ ->
            val nombrePaseadorSeleccionado = paseadorAdapter?.getItem(position)
            val paseadorSeleccionado = listaPaseadores.find { it.nombre == nombrePaseadorSeleccionado }
            paseadorSeleccionado?.let {
                actualizarRatingBars(it)
                pagoHora = it.pagoHora
                dni = it.dni
                codResidencia = it.residencia
            }
            precioTotal = actualizarPrecioTotal()
        }

        establecerDatePicker()
        establecerTimePickers()

        paymentSheet = PaymentSheet(
            this,
            ::onPaymentSheetResult
        )

        btnPagar = view.findViewById(R.id.btnPagar)
        btnPagar.setOnClickListener {
            if (validarCampos()) {
                establecerPago {
                    if (::paymentIntentClientSecret.isInitialized && ::configuracion.isInitialized) {
                        paymentSheet.presentWithPaymentIntent(
                            paymentIntentClientSecret,
                            PaymentSheet.Configuration(
                                merchantDisplayName = "Mascotas felices",
                                customer = configuracion
                            )
                        )
                    } else {
                        Toast.makeText(context, "La configuración de pago no se ha inicializado", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        return view
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                Toast.makeText(context, "Cancelado", Toast.LENGTH_LONG).show()
            }

            is PaymentSheetResult.Failed -> {
                Toast.makeText(
                    context,
                    paymentSheetResult.error.message,
                    Toast.LENGTH_LONG
                ).show()
            }

            is PaymentSheetResult.Completed -> {
                guardarPaseo()
            }
        }
    }

    private fun actualizarRatingBars(paseador: Empleado) {
        ratingBarNivelAfectuoso.rating = paseador.nivelAfectuoso.toFloat()
        ratingBarNivelAgresivo.rating = paseador.nivelAgresivo.toFloat()
        ratingBarNivelEstricto.rating = paseador.nivelEstricto.toFloat()
        ratingBarNivelEnfermos.rating = paseador.nivelEnfermos.toFloat()


        linearLayoutNivelAfectuoso.visibility = View.VISIBLE
        linearLayoutNivelAgresivo.visibility = View.VISIBLE
        linearLayoutNivelEstricto.visibility = View.VISIBLE
        linearLayoutNivelEnfermos.visibility = View.VISIBLE
    }

    private fun establecerDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Seleccionar fecha")
            .build()

        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formatoEnviar = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Listener para mostrar el DatePicker
        val fechaClickListener = View.OnClickListener {
            datePicker.show(parentFragmentManager, "datePicker")
        }

        // Asigna el listener al campo de texto y al icono de finalización
        edtxtFecha.setOnClickListener(fechaClickListener)
        txtFecha.setEndIconOnClickListener(fechaClickListener)

        // Listener para manejar la selección de la fecha
        datePicker.addOnPositiveButtonClickListener { it ->
            fecha = it
            val fechaSeleccionada = formato.format(fecha)
            edtxtFecha.setText(fechaSeleccionada)
            fechaFormateadaEnviar = formatoEnviar.format(fecha)
            if (horaInicio.isNotEmpty() && horaFin.isNotEmpty()) {
                autocompletePaseador.text = null
                listaPaseadores.clear()
                // Obtiene la lista de paseadores
                obtenerPaseadoresDisponibles { paseadores ->
                    listaPaseadores = paseadores.toMutableList()
                    val nombresPaseadores = paseadores.map { it.nombre }
                    paseadorAdapter= ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        nombresPaseadores
                    )
                    autocompletePaseador.setAdapter(paseadorAdapter)
                }

                linearLayoutNivelAfectuoso.visibility = View.GONE
                linearLayoutNivelAgresivo.visibility = View.GONE
                linearLayoutNivelEstricto.visibility = View.GONE
                linearLayoutNivelEnfermos.visibility = View.GONE

                // Actualizar precio total si las horas están definidas
                if (horaInicio.isNotEmpty() && horaFin.isNotEmpty()) {
                    precioTotal = actualizarPrecioTotal()
                }
            }
        }
    }

    private fun establecerTimePickers() {
        // Configuración para la hora de inicio
        val horaInicioListener = View.OnClickListener {
            mostrarTimePicker { hora, minuto ->
                horaInicio = String.format(Locale.getDefault(), "%02d:%02d", hora, minuto)
                edtxtHoraInicio.setText(horaInicio)
                if (horaFin.isNotEmpty()) {
                    autocompletePaseador.text = null
                    listaPaseadores.clear()
                    // Obtiene la lista de paseadores
                    obtenerPaseadoresDisponibles { paseadores ->
                        listaPaseadores = paseadores.toMutableList()
                        val nombresPaseadores = paseadores.map { it.nombre }
                        paseadorAdapter= ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            nombresPaseadores
                        )
                        autocompletePaseador.setAdapter(paseadorAdapter)
                    }

                    linearLayoutNivelAfectuoso.visibility = View.GONE
                    linearLayoutNivelAgresivo.visibility = View.GONE
                    linearLayoutNivelEstricto.visibility = View.GONE
                    linearLayoutNivelEnfermos.visibility = View.GONE

                    precioTotal = actualizarPrecioTotal()
                }
            }
        }
        edtxtHoraInicio.setOnClickListener(horaInicioListener)
        txtHoraInicio.setEndIconOnClickListener(horaInicioListener)

        // Configuración para la hora de fin
        val horaFinListener = View.OnClickListener {
            mostrarTimePicker { hora, minuto ->
                horaFin = String.format(Locale.getDefault(), "%02d:%02d", hora, minuto)
                edtxtHoraFin.setText(horaFin)
                if (horaInicio.isNotEmpty()) {
                    autocompletePaseador.text = null
                    listaPaseadores.clear()
                    // Obtiene la lista de paseadores
                    obtenerPaseadoresDisponibles { paseadores ->
                        listaPaseadores = paseadores.toMutableList()
                        val nombresPaseadores = paseadores.map { it.nombre }
                        paseadorAdapter= ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            nombresPaseadores
                        )
                        autocompletePaseador.setAdapter(paseadorAdapter)
                    }

                    linearLayoutNivelAfectuoso.visibility = View.GONE
                    linearLayoutNivelAgresivo.visibility = View.GONE
                    linearLayoutNivelEstricto.visibility = View.GONE
                    linearLayoutNivelEnfermos.visibility = View.GONE

                    precioTotal = actualizarPrecioTotal()

                }
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

    private fun actualizarPrecioTotal(): Double {
        var precio: Double = 0.0
        if (horaInicio.isNotEmpty() && horaFin.isNotEmpty() && pagoHora > 0) {
            val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())
            val inicio = formatoHora.parse(horaInicio)
            val fin = formatoHora.parse(horaFin)
            if (inicio != null && fin != null) {
                val diferencia = fin.time - inicio.time
                val horas = diferencia / (1000 * 60 * 60).toDouble()
                precio = horas * pagoHora
                txtTotal.text = String.format(Locale.getDefault(), "%.2f euros", precio)
            }
        }
        return precio
    }

    private fun obtenerPaseadoresDisponibles(callback: (List<Empleado>) -> Unit) {

        val requestQueue = Volley.newRequestQueue(requireContext())
        val listaPaseadoresUrl =
            "http://${Login.ip}/mascotasfelices/listarpaseadorescontratacion.php?fecha=$fechaFormateadaEnviar&&horaInicio=$horaInicio&&horaFin=$horaFin"
        val listaPaseadoresRequest = JsonArrayRequest(
            Request.Method.GET, listaPaseadoresUrl, null,
            { response ->
                val listaEmpleados = mutableListOf<Empleado>()
                for (i in 0 until response.length()) {
                    val empleadoObject = response.getJSONObject(i)
                    val tipoEmpleado = empleadoObject.getString("tipoEmpleado")
                    val empleado = Empleado(
                        dni = empleadoObject.getString("dni"),
                        nombre = empleadoObject.getString("nombre"),
                        direccion = empleadoObject.getString("direccion"),
                        email = empleadoObject.getString("email"),
                        tipoEmpleado = tipoEmpleado,
                        telefono = empleadoObject.getString("telefono"),
                        nivelAfectuoso = empleadoObject.getInt("nivelAfectuoso"),
                        nivelAgresivo = empleadoObject.getInt("nivelAgresivo"),
                        nivelEstricto = empleadoObject.getInt("nivelEstricto"),
                        nivelEnfermos = empleadoObject.getInt("nivelEnfermos"),
                        residencia = empleadoObject.getInt("residencia"),
                        pagoHora = if (tipoEmpleado == "Paseador") {
                            empleadoObject.getDouble("pagoHora").toFloat()
                        } else {
                            0.0f
                        },
                        salario = if (tipoEmpleado == "Cuidador") {
                            empleadoObject.getDouble("salario")
                        } else {
                            0.0
                        }
                    )
                    listaEmpleados.add(empleado)
                }
                callback(listaEmpleados)
            },
            { _ ->
                Toast.makeText(
                    requireContext(),
                    "No hay paseadores disponibles",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
        requestQueue.add(listaPaseadoresRequest)
    }

    /**
     * Método para obtener las mascotas del cliente
     */
    private fun obtenerMascotas(callback: (List<Mascota>) -> Unit) {

        val codCliente = Login.cliente?.codCliente.toString()

        val requestQueue: RequestQueue = Volley.newRequestQueue(requireContext())
        val url =
            "http://${Login.ip}/mascotasfelices/listarmascotascliente.php?codCliente=$codCliente"
        val resultado = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val mascotas = mutableListOf<Mascota>()
                for (i in 0 until response.length()) {
                    val mascotaObject = response.getJSONObject(i)
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
                    mascotas.add(mascota)
                }
                callback(mascotas)
            },
            { _ ->
                Toast.makeText(
                    context,
                    "Error al obtener las mascotas",
                    Toast.LENGTH_LONG
                ).show()
            })
        requestQueue.add(resultado)
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

    private fun establecerPago(callback: () -> Unit) {
        val url = "http://${Login.ip}/stripe_api/index.php"
        val requestQueue: RequestQueue = Volley.newRequestQueue(requireContext())
        val resultadoPost = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                val jsonResponse = JSONObject(response)
                paymentIntentClientSecret = jsonResponse.getString("paymentIntent")
                configuracion = PaymentSheet.CustomerConfiguration(
                    jsonResponse.getString("customer"),
                    jsonResponse.getString("ephemeralKey")
                )
                PaymentConfiguration.init(
                    requireContext(),
                    jsonResponse.getString("publishableKey")
                )
                callback()
            }, Response.ErrorListener { _ ->
                Toast.makeText(context, "No se ha podido realizar el pago", Toast.LENGTH_LONG)
                    .show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros["nombre"] = Login.cliente!!.nombre
                parametros["cantidad"] = txtTotal.text.toString()
                return parametros
            }
        }
        requestQueue.add(resultadoPost)
    }

    private fun validarCampos(): Boolean {
        var esValido = true

        if (edtxtFecha.text.isNullOrEmpty()) {
            txtFecha.error = "Seleccione un día"
            esValido = false
        } else {
            txtFecha.error = null
        }

        if (autocompleteMascota.text.isNullOrEmpty()) {
            txtMascota.error = "Selecciona una mascota"
            esValido = false
        } else {
            txtMascota.error = null
        }

        if (edtxtHoraFin.text.isNullOrEmpty()) {
            txtHoraFin.error = "Seleccione una hora de finalización"
            esValido = false
        } else {
            txtHoraFin.error = null
        }
        if (edtxtHoraInicio.text.isNullOrEmpty()) {
            txtHoraInicio.error = "Seleccione una hora de inicio"
            esValido = false
        } else {
            txtHoraInicio.error = null

        }
        return esValido
    }

    private fun guardarPaseo() {

        val codMascota = obtenerCodMascotaSeleccionado()

        val url = "http://${Login.ip}/mascotasfelices/insertarpaseo.php"
        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        val resultadoPost = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                if (response.trim() == "success") {
                    Toast.makeText(context, "Pago realizado con éxito", Toast.LENGTH_LONG).show()
                    parentFragmentManager.beginTransaction().apply {
                        replace(R.id.frameLayoutCliente, InicioCliente())
                        commit()
                    }
                } else {
                    Toast.makeText(context, "Error al realizar el pago", Toast.LENGTH_LONG).show()
                }
            }, Response.ErrorListener { _ ->
                Toast.makeText(
                    context, "No se ha podido guardar el paseo", Toast.LENGTH_LONG
                ).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros["fecha"] = fechaFormateadaEnviar
                parametros["mascota"] = codMascota.toString()
                parametros["paseador"] = dni
                parametros["horaInicio"] = horaInicio
                parametros["horaFin"] = horaFin
                parametros["codResidencia"] = codResidencia.toString()
                return parametros
            }
        }
        requestQueue.add(resultadoPost)
    }

    /**
     * Método para obtener el codMascota seleccionado del autocomplete
     */
    private fun obtenerCodMascotaSeleccionado(): Int? {
        val nombreMascotaSeleccionada = autocompleteMascota.text.toString()
        val mascotaSeleccionada = listaMascotas.find { it.nombre == nombreMascotaSeleccionada }
        return mascotaSeleccionada?.codMascota
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Paseos().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}