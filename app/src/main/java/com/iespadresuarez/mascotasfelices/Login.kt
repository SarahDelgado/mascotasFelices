package com.iespadresuarez.mascotasfelices

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.iespadresuarez.mascotasfelices.modelosBd.Cliente
import com.iespadresuarez.mascotasfelices.modelosBd.Empleado

/**
 * Actividad que gestiona el proceso de inicio de sesión de usuarios y empleados.
 */
class Login : AppCompatActivity() {

    companion object {
        var cliente: Cliente? = null // Cliente logueado actualmente
        var empleado: Empleado? = null // Empleado logueado actualmente
        var ip: String = "192.168.1.133" // IP del servidor donde se aloja la base de datos
    }

    // Declaración de vistas
    private lateinit var txtEmail: TextInputLayout
    private lateinit var txtPassword: TextInputLayout

    private lateinit var edtxtEmail: TextInputEditText
    private lateinit var edtxtPassword: TextInputEditText

    private lateinit var txtRegistrar: TextView

    private lateinit var btnLogin: Button

    /**
     * Método llamado cuando se crea la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializa las vistas
        txtEmail = findViewById(R.id.txtEmail)
        txtPassword = findViewById(R.id.txtPassword)
        edtxtEmail = findViewById(R.id.edtxtEmail)
        edtxtPassword = findViewById(R.id.edtxtPassword)
        txtRegistrar = findViewById(R.id.txtRegistrar)
        btnLogin = findViewById(R.id.btnLogin)

        // Configuración de listeners para los campos de email y contraseña
        edtxtEmail.addTextChangedListener(crearTextWatcher(txtEmail))
        edtxtPassword.addTextChangedListener(crearTextWatcher(txtPassword))

        // Define la acción del texto "Crear cuenta"
        txtRegistrar.setOnClickListener {
            registrar()
        }

        // Define la acción del botón de "Iniciar Sesion"
        btnLogin.setOnClickListener {
            login()
        }

    }

    /**
     * Método llamado justo antes de que la actividad sea visible al usuario.
     * Verifica si ya hay un cliente o empleado logueado y navega a la pantalla principal correspondiente.
     */
    public override fun onStart() {
        super.onStart()
        if (cliente != null) {
            irMainCliente()
        } else if (empleado?.tipoEmpleado == "Admin") {
            irMainAdmin()
        } else if (empleado?.tipoEmpleado == "Paseador" || empleado?.tipoEmpleado == "Cuidador") {
            irMainEmpleado()
        }
    }

    /**
     * Crea un TextWatcher que maneja los cambios de texto en un TextInputLayout específico.
     * @param textInputLayout TextInputLayout al que se asociará el TextWatcher.
     * @return TextWatcher creado.
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
     * Método invocado al hacer clic en el botón de "Login".
     * Inicia el proceso de login obteniendo los valores de email y contraseña ingresados por el usuario.
     */
    private fun login() {
        val email: String = edtxtEmail.text.toString()
        val password: String = edtxtPassword.text.toString()

        val requestQueue = Volley.newRequestQueue(this)
        val clienteUrl =
            "http://$ip/mascotasfelices/buscarcliente.php?email=$email&password=$password"
        val empleadoUrl =
            "http://$ip/mascotasfelices/buscarempleado.php?email=$email&password=$password"
        buscarCliente(requestQueue, clienteUrl, empleadoUrl)
    }

    /**
     * Realiza una solicitud para buscar un cliente por su email y contraseña en la base de datos.
     * @param requestQueue Cola de solicitudes Volley.
     * @param clienteUrl URL de la solicitud para buscar cliente.
     * @param empleadoUrl URL de la solicitud para buscar empleado si el cliente no existe.
     */
    private fun buscarCliente(requestQueue: RequestQueue, clienteUrl: String, empleadoUrl: String) {

        val clienteRequest = JsonObjectRequest(
            Request.Method.GET, clienteUrl, null,
            { response ->
                if (response.has("mensaje") && response.getString("mensaje") == "error") {
                    buscarEmpleado(requestQueue, empleadoUrl)
                } else {
                    // Crear objeto Cliente a partir de los datos JSON obtenidos
                    cliente = Cliente(
                        codCliente = response.getInt("codCliente"),
                        email = response.getString("email"),
                        nombre = response.getString("nombre"),
                        direccion = if (response.has("direccion") && !response.isNull("direccion")) response.getString(
                            "direccion"
                        ) else "",
                        telefono1 = if (response.has("telefono1") && !response.isNull("telefono1")) response.getString(
                            "telefono1"
                        ) else "",
                        telefono2 = if (response.has("telefono2") && !response.isNull("telefono2")) response.getString(
                            "telefono2"
                        ) else ""
                    )
                    // Navega a la pantalla principal del cliente
                    irMainCliente()
                }
            },
            { _ ->
                // Muestra errores de login en caso de fallo en la solicitud
                mostrarErrorLogin()
                txtEmail.error = "Email y/o contraseña incorrectos"
                txtPassword.error = "Email y/o contraseña incorrectos"
            }
        )
        requestQueue.add(clienteRequest)
    }

    /**
     * Realiza una solicitud para buscar un empleado por su email y contraseña en la base de datos.
     * @param requestQueue Cola de solicitudes Volley.
     * @param empleadoUrl URL de la solicitud para buscar empleado.
     */
    private fun buscarEmpleado(requestQueue: RequestQueue, empleadoUrl: String) {
        val empleadoRequest = JsonObjectRequest(
            Request.Method.GET, empleadoUrl, null,
            { response ->
                if (response.has("mensaje") && response.getString("mensaje") == "error") {
                    // Muestra errores de login si no se encontró cliente ni empleado
                    mostrarErrorLogin()
                } else {
                    empleado = Empleado(
                        dni = response.getString("dni"),
                        nombre = response.getString("nombre"),
                        direccion = if (response.has("direccion") && !response.isNull("direccion")) response.getString(
                            "direccion"
                        ) else "",
                        email = response.getString("email"),
                        tipoEmpleado = response.getString("tipoEmpleado"),
                        telefono = if (response.has("telefono") && !response.isNull("telefono")) response.getString(
                            "telefono"
                        ) else "",
                        nivelAfectuoso = response.getInt("nivelAfectuoso"),
                        nivelAgresivo = response.getInt("nivelAgresivo"),
                        nivelEstricto = response.getInt("nivelEstricto"),
                        nivelEnfermos = response.getInt("nivelEnfermos"),
                        residencia = response.getInt("residencia"),
                        pagoHora = if (response.has("pagoHora") && !response.isNull("pagoHora")) response.getDouble(
                            "pagoHora"
                        ).toFloat() else 0.0f,
                        salario = if (response.has("salario") && !response.isNull("salario")) response.getDouble(
                            "salario"
                        ) else 0.0
                    )
                    when (empleado?.tipoEmpleado) {
                        "Admin" -> {
                            // Navega a la pantalla principal del administrador
                            irMainAdmin()
                        }

                        "Paseador", "Cuidador" -> {
                            // Navega a la pantalla principal del empleado
                            irMainEmpleado()
                        }

                    }
                }
            },
            { _ ->
                // Muestra errores de login en caso de fallo en la solicitud
                mostrarErrorLogin()
            }
        )
        requestQueue.add(empleadoRequest)
    }

    /**
     * Navega a la pantalla principal del cliente.
     */
    private fun irMainCliente() {
        val intent = Intent(this, MainActivityCliente::class.java)
        startActivity(intent)
        finish() // Cierra la actividad actual para evitar que el usuario vuelva atrás
    }

    /**
     * Navega a la pantalla principal del administrador.
     */
    private fun irMainAdmin() {
        val intent = Intent(this, MainActivityAdministrador::class.java)
        startActivity(intent)
        finish() // Cierra la actividad actual para evitar que el usuario vuelva atrás
    }

    /**
     * Navega a la pantalla principal del empleado.
     */
    private fun irMainEmpleado() {
        val intent = Intent(this, MainActivityEmpleado::class.java)
        startActivity(intent)
        finish() // Cierra la actividad actual para evitar que el usuario vuelva atrás
    }

    /**
     * Muestra errores de login en los campos de email y contraseña.
     */
    private fun mostrarErrorLogin() {
        txtEmail.error = "Email y/o contraseña incorrectos"
        txtPassword.error = "Email y/o contraseña incorrectos"
    }

    /**
     * Inicia la actividad de registro para que un nuevo usuario se registre en la aplicación.
     */
    private fun registrar() {
        val intent = Intent(this, Registro::class.java)
        startActivity(intent)
        finish() // Cierra la actividad actual para evitar que el usuario vuelva atrás
    }

}
