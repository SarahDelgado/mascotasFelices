package com.iespadresuarez.mascotasfelices.fragmentosCliente

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
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
import com.iespadresuarez.mascotasfelices.modelosBd.Mascota
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.temporal.ChronoUnit
import kotlin.properties.Delegates

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Hospedajes : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var btnPagar: Button
    private lateinit var btnComprobarDisponibilidad: Button

    private lateinit var paymentSheet: PaymentSheet
    private lateinit var paymentIntentClientSecret: String
    private lateinit var configuracion: PaymentSheet.CustomerConfiguration

    private lateinit var edtxtFecha: TextInputEditText
    private lateinit var autocompleteMascota: MaterialAutoCompleteTextView

    private lateinit var txtFecha: TextInputLayout
    private lateinit var txtMascota: TextInputLayout

    private lateinit var listaMascotas: MutableList<Mascota>
    private lateinit var mascotaAdapter: ArrayAdapter<String>

    private lateinit var txtTotal: TextView

    private var fechaInicio: LocalDate? = null
    private var fechaFin: LocalDate? = null
    private var fechaInicioEnviar: String? = null
    private var fechaFinEnviar: String? = null

    private var precioTotal by Delegates.notNull<Double>()

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
        val view = inflater.inflate(R.layout.fragment_hospedajes, container, false)

        edtxtFecha = view.findViewById(R.id.edtxtFecha)
        autocompleteMascota = view.findViewById(R.id.autocompleteMascota)

        txtFecha = view.findViewById(R.id.txtFecha)
        txtMascota = view.findViewById(R.id.txtMascota)

        txtTotal = view.findViewById(R.id.txtTotal)

        autocompleteMascota.addTextChangedListener(crearTextWatcher(txtMascota))
        edtxtFecha.addTextChangedListener(crearTextWatcher(txtFecha))

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

        establecerDateRangePicker()

        paymentSheet = PaymentSheet(
            this,
            ::onPaymentSheetResult
        )

        btnComprobarDisponibilidad = view.findViewById(R.id.btnComprobarDisponibilidad)
        btnComprobarDisponibilidad.setOnClickListener {
            comprobarDisponibilidad()
        }

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
                        Toast.makeText(
                            context,
                            "La configuración de pago no se ha inicializado",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        return view
    }

    /**
     * Comprueba si hay habitacion disponible para la mascota seleccionada
     */
    private fun comprobarDisponibilidad() {
        val codMascota = obtenerCodMascotaSeleccionado()
        val fechaInicioStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(fechaInicio!!)
        val fechaFinStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(fechaFin!!)

        val url =
            "http://${Login.ip}/mascotasfelices/comprobarDisponibilidad.php?codMascota=$codMascota&fechaInicio=$fechaInicioStr&fechaFin=$fechaFinStr"

        val requestQueue: RequestQueue = Volley.newRequestQueue(requireContext())
        val disponibilidadRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                // Verificar la respuesta para determinar la disponibilidad
                if (response.length() > 0) {
                    // Habitación disponible
                    Toast.makeText(context, "Habitación disponible", Toast.LENGTH_SHORT).show()
                } else {
                    // Habitación no disponible
                    Toast.makeText(context, "Habitación no disponible", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(
                    context,
                    "Error al comprobar disponibilidad: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
        requestQueue.add(disponibilidadRequest)
    }

    private fun establecerDateRangePicker() {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Seleccionar fechas")
            .build()

        val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
        val formatoEnviar = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

        // Listener para mostrar el DatePicker
        val fechaClickListener = View.OnClickListener {
            datePicker.show(parentFragmentManager, "datePicker")
        }

        // Asigna el listener al campo de texto y al icono de finalización
        edtxtFecha.setOnClickListener(fechaClickListener)
        txtFecha.setEndIconOnClickListener(fechaClickListener)

        // Listener para manejar la selección del rango de fechas
        datePicker.addOnPositiveButtonClickListener { selection ->
            if (selection != null) {
                val fechaInicioMillis = selection.first
                val fechaFinMillis = selection.second

                if (fechaInicioMillis != null && fechaFinMillis != null) {
                    fechaInicio = Instant.ofEpochMilli(fechaInicioMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    fechaFin = Instant.ofEpochMilli(fechaFinMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()

                    val fechaInicioStr = fechaInicio!!.format(formato)
                    val fechaFinStr = fechaFin!!.format(formato)

                    fechaInicioEnviar = fechaInicio!!.format(formatoEnviar)
                    fechaFinEnviar = fechaFin!!.format(formatoEnviar)

                    edtxtFecha.setText("$fechaInicioStr - $fechaFinStr")

                    precioTotal = actualizarPrecioTotal()

                    if (validarCampos()) {
                        comprobarDisponibilidad()
                    }
                }
            }
        }
    }

    private fun actualizarPrecioTotal(): Double {
        val precioPorDia = 17.0
        var precioTotal = 0.0

        if (fechaInicio != null && fechaFin != null) {
            val dias = ChronoUnit.DAYS.between(fechaInicio, fechaFin?.plusDays(1)) // Incluye el día final
            precioTotal = dias * precioPorDia
            txtTotal.text = String.format(Locale.getDefault(), "%.2f euros", precioTotal)
        }

        return precioTotal
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
                guardarHospedaje()
            }
        }
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
        return esValido
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

    private fun guardarHospedaje() {

        val codMascota = obtenerCodMascotaSeleccionado()

        val url = "http://${Login.ip}/mascotasfelices/insertarHospedaje.php"
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
                    context, "No se ha podido guardar el hospedaje", Toast.LENGTH_LONG
                ).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros["codResidencia"] = "1" // Aquí debe ser dinámico
                //parametros["numHabitacion"] = numHabitacion
                parametros["codMascota"] = codMascota.toString()
                parametros["fechaInicio"] = fechaInicioEnviar!!
                parametros["fechaFin"] = fechaFinEnviar!!
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
            Hospedajes().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}
