package com.iespadresuarez.mascotasfelices

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.iespadresuarez.mascotasfelices.modelosBd.Mascota
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates

class Mascota : AppCompatActivity() {

    private lateinit var edtxtNombre: TextInputEditText
    private lateinit var autocompleteTipoMascota: AutoCompleteTextView
    private lateinit var edtxtRaza: TextInputEditText
    private lateinit var edtxtFechNac: TextInputEditText
    private lateinit var edtxtPeso: TextInputEditText
    private lateinit var autocompleteEsterilizado: AutoCompleteTextView
    private lateinit var edtxtEdad: TextInputEditText

    private lateinit var txtNombre: TextInputLayout
    private lateinit var txtTipoMascota: TextInputLayout
    private lateinit var txtRaza: TextInputLayout
    private lateinit var txtFechNac: TextInputLayout
    private lateinit var txtPeso: TextInputLayout
    private lateinit var txtEsterilizado: TextInputLayout
    private lateinit var txtTitulo: TextView
    private lateinit var txtEdad: TextInputLayout

    private lateinit var btnGuardar: Button
    private lateinit var btnBorrar: Button

    private var datePickerDialog: DatePickerDialog? = null

    var codMascota by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mascota)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.linearLayoutMascota)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicialización de los componentes de la interfaz de usuario
        edtxtNombre = findViewById(R.id.edtxtNombre)
        autocompleteTipoMascota = findViewById(R.id.autocompleteTipoMascota)
        edtxtRaza = findViewById(R.id.edtxtRaza)
        edtxtFechNac = findViewById(R.id.edtxtFechNac)
        edtxtPeso = findViewById(R.id.edtxtPeso)
        autocompleteEsterilizado = findViewById(R.id.autocompleteEsterilizado)
        edtxtEdad = findViewById(R.id.edtxtEdad)

        txtNombre = findViewById(R.id.txtNombre)
        txtTipoMascota = findViewById(R.id.txtTipoMascota)
        txtRaza = findViewById(R.id.txtRaza)
        txtFechNac = findViewById(R.id.txtFechNac)
        txtPeso = findViewById(R.id.txtPeso)
        txtEsterilizado = findViewById(R.id.txtEsterilizado)
        txtEdad = findViewById(R.id.txtEdad)

        txtTitulo = findViewById(R.id.txtTitulo)

        btnGuardar = findViewById(R.id.btnGuardar)
        btnBorrar = findViewById(R.id.btnBorrar)

        // Configurar el DatePickerDialog
        configurarDatePicker()

        // Mostrar el DatePickerDialog cuando se haga clic en edtxtFechNac
        edtxtFechNac.setOnClickListener {
            datePickerDialog?.show()
        }

        // Configura el dropdown del tipo de mascota
        val tipoMascota = resources.getStringArray(R.array.tiposMascota)
        val adapterMascota = ArrayAdapter(this, R.layout.dropdown_menu_popup_item, tipoMascota)
        autocompleteTipoMascota.setAdapter(adapterMascota)

        // Configura el dropdown de esterilizado
        val confirmacion = resources.getStringArray(R.array.confirmacion)
        val adapterEsterilizado =
            ArrayAdapter(this, R.layout.dropdown_menu_popup_item, confirmacion)
        autocompleteEsterilizado.setAdapter(adapterEsterilizado)

        // Si se pasan los datos
        if (intent.hasExtra("codMascota")) {
            // Llama a la función para obtener los detalles de la mascota
            obtenerDetallesMascota()
            txtTitulo.visibility = View.GONE
            btnGuardar.visibility = View.GONE
            if (Login.cliente != null) {
                // Establece todos las vistas no habilitados
                setComponentsEnabled(false)
                btnBorrar.visibility = View.VISIBLE
                btnBorrar.setOnClickListener {
                    borrar()
                }
            } else if ( Login.empleado != null) {
                // Establece todos las vistas no habilitados
                setComponentsEnabled(false)
                txtFechNac.visibility = View.GONE
                txtEdad.visibility = View.VISIBLE
                edtxtEdad.setText(calcularEdadMascota().toString())
            }
        } else {
            btnGuardar.setOnClickListener {
                guardar()
            }
        }
    }

    /**
     * Método para habilitar o deshabilitar las vistas del Activity
     */
    private fun setComponentsEnabled(enabled: Boolean) {
        edtxtNombre.isEnabled = enabled
        autocompleteTipoMascota.isEnabled = enabled
        edtxtRaza.isEnabled = enabled
        edtxtFechNac.isEnabled = enabled
        edtxtPeso.isEnabled = enabled
        autocompleteEsterilizado.isEnabled = enabled
        edtxtEdad.isEnabled = enabled
    }

    /**
     * Método para calcular la edad de la mascota
     */
    private fun calcularEdadMascota(): Int {
        val fechaNacimientoStr = edtxtFechNac.text.toString().trim()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val fechaNac = LocalDate.parse(fechaNacimientoStr, formatter)
        val fechaActual = LocalDate.now()
        val periodo = Period.between(fechaNac, fechaActual)
        return periodo.years
    }

    private fun configurarDatePicker() {
        // Obtener la fecha actual
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Configurar el DatePickerDialog
        datePickerDialog = DatePickerDialog(
            this,
            { _, yearSelected, monthOfYear, dayOfMonth ->
                // Actualizar el campo de texto con la fecha seleccionada
                val selectedDate = Calendar.getInstance()
                selectedDate.set(yearSelected, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                edtxtFechNac.setText(dateFormat.format(selectedDate.time))
            },
            year,
            month,
            day
        )
    }

    /**
     * Obtiene los detalles de la mascota desde el servidor y actualiza la UI.
     */
    private fun obtenerDetallesMascota() {
        // Obtiene el código de la mascota de la intención
        codMascota = intent.getIntExtra("codMascota", 0)

        val requestQueue = Volley.newRequestQueue(this)
        val url = "http://${Login.ip}/mascotasfelices/buscarmascota.php?codMascota=$codMascota"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val mascota = Mascota(
                    codMascota = response.getInt("codMascota"),
                    nombre = response.getString("nombre"),
                    fechNac = LocalDate.parse(
                        response.getString("fechNac"),
                        DateTimeFormatter.ISO_DATE
                    ),
                    tipoMascota = response.getString("tipoMascota"),
                    raza = response.getString("raza"),
                    esterilizado = response.getInt("esterilizado"),
                    peso = response.getDouble("peso").toFloat(),
                    propietario = response.getInt("propietario")
                )
                // Actualiza los campos de la interfaz con los datos obtenidos
                edtxtNombre.setText(mascota.nombre)
                autocompleteTipoMascota.setText(mascota.tipoMascota, false)
                edtxtRaza.setText(mascota.raza)
                edtxtFechNac.setText(mascota.fechNac.toString())
                edtxtPeso.setText(mascota.peso.toString())
                autocompleteEsterilizado.setText(
                    if (mascota.esterilizado == 0) "No" else "Sí",
                    false
                )
            },
            { _ ->
                Toast.makeText(this, "Error al obtener detalles de la mascota", Toast.LENGTH_LONG)
                    .show()
            }
        )
        requestQueue.add(jsonObjectRequest)
    }

    /**
     * Valida los campos del formulario de la mascota.
     * @return `true` si todos los campos son válidos, `false` en caso contrario.
     */
    private fun validarCampos(): Boolean {
        var esValido = true

        if (edtxtNombre.text.isNullOrEmpty()) {
            txtNombre.error = "Introduce un nombre válido"
            esValido = false
        } else {
            txtNombre.error = null
        }

        if (edtxtFechNac.text.isNullOrEmpty()) {
            txtFechNac.error = "Introduce una fecha de nacimiento válida"
            esValido = false
        } else {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaNacimiento = dateFormat.parse(edtxtFechNac.text.toString())
            if (fechaNacimiento!!.after(Date())) {
                txtFechNac.error = "La fecha de nacimiento no puede ser en el futuro"
                esValido = false
            } else {
                txtFechNac.error = null
            }
        }

        if (edtxtRaza.text.isNullOrEmpty()) {
            txtRaza.error = "Introduce una raza"
            esValido = false
        } else {
            txtRaza.error = null
        }

        if (edtxtPeso.text.isNullOrEmpty() || edtxtPeso.text.toString().toFloatOrNull() == null) {
            txtPeso.error = "Introduce un peso válido"
            esValido = false
        } else {
            txtPeso.error = null
        }

        if (autocompleteTipoMascota.text.isNullOrEmpty()) {
            txtTipoMascota.error = "Selecciona un tipo de mascota"
            esValido = false
        } else {
            txtTipoMascota.error = null
        }

        if (autocompleteEsterilizado.text.isNullOrEmpty()) {
            txtEsterilizado.error = "Selecciona una opción"
            esValido = false
        } else {
            txtEsterilizado.error = null
        }

        return esValido
    }

    /**
     * Guarda los datos de la mascota en la base de datos.
     */
    private fun guardar() {

        if (validarCampos()) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fecha = dateFormat.parse(edtxtFechNac.text.toString().trim())
            val nombre = edtxtNombre.text.toString().trim()
            val fechNac =
                fecha?.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it) }
            val tipoMascota = autocompleteTipoMascota.text.toString().trim()
            val raza = edtxtRaza.text.toString().trim()
            val esterilizado = autocompleteEsterilizado.text.toString().trim() == "Sí"
            val peso = edtxtPeso.text.toString().trim().toFloat()
            val propietario = Login.cliente?.codCliente.toString()

            val url = "http://${Login.ip}/mascotasfelices/insertarmascota.php"
            val requestQueue = Volley.newRequestQueue(this)
            val resultadoPost = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("mensaje")
                    if (status == "success") {
                        Toast.makeText(this, "Mascota guardada con éxito", Toast.LENGTH_LONG).show()
                        limpiarCampos()
                    } else if (status == "error") {
                        Toast.makeText(
                            this,
                            "No se ha podido guardar la mascota",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                Response.ErrorListener { _ ->
                    Toast.makeText(this, "No se ha podido guardar la mascota", Toast.LENGTH_LONG)
                        .show()
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val parametros = HashMap<String, String>()
                    parametros["nombre"] = nombre
                    parametros["fechNac"] = fechNac.toString()
                    parametros["tipoMascota"] = tipoMascota
                    parametros["raza"] = raza
                    parametros["esterilizado"] = if (esterilizado) "1" else "0"
                    parametros["peso"] = peso.toString()
                    parametros["propietario"] = propietario
                    return parametros
                }
            }
            requestQueue.add(resultadoPost)
        }
    }

    private fun borrar(){
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.borrarMascota))
            .setMessage(resources.getString(R.string.seguroBorrar))
            .setNegativeButton(resources.getString(R.string.cancelar)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.Aceptar)) { dialog, which ->
                borrarMascota()
            }
            .show()
    }

    /**
     * Borra la mascota de la base de datos.
     */
    private fun borrarMascota() {
            val url = "http://${Login.ip}/mascotasfelices/borrarmascota.php"
            val requestQueue = Volley.newRequestQueue(this)
            val resultadoPost = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("mensaje")
                    if (status == "success") {
                        Toast.makeText(this, "Mascota borrada con éxito", Toast.LENGTH_LONG).show()
                        limpiarCampos()
                    } else if (status == "error") {
                        Toast.makeText(this, "No se ha podido borrar la mascota", Toast.LENGTH_LONG)
                            .show()
                    }
                },
                Response.ErrorListener { _ ->
                    Toast.makeText(this, "No se ha podido borrar la mascota", Toast.LENGTH_LONG)
                        .show()
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val parametros = HashMap<String, String>()
                    parametros["codMascota"] = codMascota.toString()
                    return parametros
                }
            }
            requestQueue.add(resultadoPost)
    }

    /**
     * Limpia los campos del formulario.
     */
    private fun limpiarCampos() {
        edtxtNombre.text = null
        edtxtFechNac.text = null
        autocompleteTipoMascota.text = null
        edtxtRaza.text = null
        edtxtPeso.text = null
        autocompleteEsterilizado.text = null
    }


}