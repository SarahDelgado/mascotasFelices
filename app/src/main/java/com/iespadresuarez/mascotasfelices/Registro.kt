package com.iespadresuarez.mascotasfelices

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.iespadresuarez.mascotasfelices.modelosBd.Cliente
import org.json.JSONObject

/**
 * Clase que maneja la Activity del Registro del usuario
 */
class Registro : AppCompatActivity() {

    private lateinit var edtxtEmail: TextInputEditText
    private lateinit var edtxtPassword: TextInputEditText
    private lateinit var edtxtPasswordComprobacion: TextInputEditText
    private lateinit var edtxtNombre: TextInputEditText
    private lateinit var edtxtDireccion: TextInputEditText
    private lateinit var edtxtTelefono: TextInputEditText

    private lateinit var txtEmail: TextInputLayout
    private lateinit var txtPassword: TextInputLayout
    private lateinit var txtPasswordComprobacion: TextInputLayout
    private lateinit var txtNombre: TextInputLayout
    private lateinit var txtDireccion: TextInputLayout
    private lateinit var txtTelefono: TextInputLayout

    private lateinit var cbAceptar: CheckBox
    private lateinit var lyTerminos: LinearLayout
    private lateinit var txtTerminos: TextView

    private lateinit var btnRegistrar : Button

    /**
     * Método que se llama cuando se crea la actividad.
     * @param savedInstanceState Si la actividad se está re-inicializando después de ser apagada,
     * este paquete contiene los datos que proporcionó más recientemente en onSaveInstanceState(Bundle).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollViewRegistro)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa las vistas
        edtxtEmail = findViewById(R.id.edtxtEmail)
        edtxtPassword = findViewById(R.id.edtxtPassword)
        edtxtPasswordComprobacion = findViewById(R.id.edtxtPasswordComprobacion)
        edtxtNombre = findViewById(R.id.edtxtNombre)
        edtxtDireccion = findViewById(R.id.edtxtDireccion)
        edtxtTelefono = findViewById(R.id.edtxtTelefono)
        cbAceptar = findViewById(R.id.cbAceptar)
        lyTerminos = findViewById(R.id.lyTerminos)
        txtTerminos = findViewById(R.id.txtTerminos)
        btnRegistrar = findViewById(R.id.btnRegistrar)

        txtEmail = findViewById(R.id.txtEmail)
        txtPassword = findViewById(R.id.txtPassword)
        txtPasswordComprobacion = findViewById(R.id.txtPasswordComprobacion)
        txtNombre = findViewById(R.id.txtNombre)
        txtDireccion = findViewById(R.id.txtDireccion)
        txtTelefono = findViewById(R.id.txtTelefono)

        // Crea los Listener de los componentes
        edtxtEmail.addTextChangedListener(crearTextWatcher(txtEmail))
        edtxtPassword.addTextChangedListener(crearTextWatcher(txtPassword))
        edtxtPasswordComprobacion.addTextChangedListener(crearTextWatcher(txtPasswordComprobacion))
        edtxtNombre.addTextChangedListener(crearTextWatcher(txtNombre))
        edtxtDireccion.addTextChangedListener(crearTextWatcher(txtDireccion))
        edtxtTelefono.addTextChangedListener(crearTextWatcher(txtTelefono))

        // Define la acción del texto de los términos y condiciones
        txtTerminos.setOnClickListener {
            irTerminos()
        }

        // Define la acción del botón de registrar
        btnRegistrar.setOnClickListener {
            registrar()
        }

    }

    /**
     * Crea un TextWatcher para limpiar los errores al escribir en los campos.
     * @param textInputLayout El layout del campo de texto que será observado.
     * @return Un TextWatcher que limpia el error del TextInputLayout asociado.
     */
    private fun crearTextWatcher(textInputLayout: TextInputLayout): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textInputLayout.error = null
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    /**
     * Valida los campos del formulario de registro.
     * @return Verdadero si todos los campos son válidos, falso en caso contrario.
     */
    private fun validarCampos(): Boolean {
        var esValido = true

        if (edtxtEmail.text.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(edtxtEmail.text!!)
                .matches()
        ) {
            txtEmail.error = "Introduce un correo electrónico válido"
            esValido = false
        } else {
            edtxtEmail.error = null
        }

        if (edtxtPassword.text.isNullOrEmpty() || edtxtPassword.text!!.length < 6) {
            txtPassword.error = "La contraseña debe tener al menos 6 caracteres"
            esValido = false
        } else {
            edtxtPassword.error = null
        }

        if (edtxtPasswordComprobacion.text.isNullOrEmpty() || edtxtPasswordComprobacion.text.toString() != edtxtPassword.text.toString()) {
            txtPasswordComprobacion.error = "Las contraseñas no coinciden"
            esValido = false
        } else {
            edtxtPasswordComprobacion.error = null
        }

        if (edtxtNombre.text.isNullOrEmpty()) {
            txtNombre.error = "Introduce tu nombre"
            esValido = false
        } else {
            edtxtNombre.error = null
        }

        if (edtxtDireccion.text.isNullOrEmpty()) {
            txtDireccion.error = "Introduce tu dirección"
            esValido = false
        } else {
            edtxtDireccion.error = null
        }

        if (edtxtTelefono.text.isNullOrEmpty() || !Patterns.PHONE.matcher(edtxtTelefono.text!!)
                .matches() || edtxtTelefono.text!!.length != 9
        ) {
            txtTelefono.error = "Introduce un número de teléfono válido"
            esValido = false
        } else {
            edtxtTelefono.error = null
        }

        if (!cbAceptar.isChecked) {
            Toast.makeText(this, "Debes aceptar los términos y condiciones", Toast.LENGTH_SHORT)
                .show()
            esValido = false
        }

        return esValido
    }

    /**
     * Registra un nuevo usuario en la base de datos.
     * Si la validación de campos es exitosa, envía una solicitud al servidor para registrar el nuevo usuario.
     */
    private fun registrar() {
        if (validarCampos()) {
            val email = edtxtEmail.text.toString().trim()
            val password = edtxtPassword.text.toString().trim()
            val nombre = edtxtNombre.text.toString().trim()
            val direccion = edtxtDireccion.text.toString().trim()
            val telefono = edtxtTelefono.text.toString().trim()
            val url = "http://${Login.ip}/mascotasfelices/insertarcliente.php"
            val requestQueue: RequestQueue = Volley.newRequestQueue(this)
            val resultadoPost = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("mensaje")
                    if (status == "success") {
                        val codCliente = jsonResponse.getInt("codCliente")
                        // Guarda el cliente en la variable Login.cliente
                        Login.cliente = Cliente(
                            codCliente = codCliente,
                            email = email,
                            nombre = nombre,
                            direccion = direccion,
                            telefono1 = telefono
                        )
                        // Registro exitoso, navega a MainActivityCliente
                        irMainCliente()
                    } else if (status == "error") {
                        // Muestra un mensaje de error
                        Toast.makeText(
                            this,
                            "No se ha podido realizar el registro",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }, Response.ErrorListener { _ ->
                    // Muestra un mensaje de error
                    Toast.makeText(
                        this, "No se ha podido realizar el registro", Toast.LENGTH_LONG
                    ).show()
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val parametros = HashMap<String, String>()
                    parametros["email"] = email
                    parametros["password"] = password
                    parametros["nombre"] = nombre
                    parametros["direccion"] = direccion
                    parametros["telefono"] = telefono
                    return parametros
                }
            }
            requestQueue.add(resultadoPost)
        }
    }

    /**
     * Navega a la actividad de Términos y Condiciones.
     */
    private fun irTerminos() {
        val intent = Intent(this, Terminos::class.java)
        startActivity(intent)
    }

    /**
     * Navega a la actividad main del cliente.
     */
    private fun irMainCliente() {
        val intent = Intent(this, MainActivityCliente::class.java)
        startActivity(intent)
    }

}