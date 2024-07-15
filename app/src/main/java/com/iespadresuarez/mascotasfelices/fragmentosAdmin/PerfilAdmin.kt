package com.iespadresuarez.mascotasfelices.fragmentosAdmin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.iespadresuarez.mascotasfelices.Login
import com.iespadresuarez.mascotasfelices.R
import com.iespadresuarez.mascotasfelices.modelosBd.Empleado
import org.json.JSONObject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PerfilAdmin : Fragment() {

    private lateinit var edtxtDireccion: TextInputEditText
    private lateinit var edtxtTelefono: TextInputEditText
    private lateinit var edtxtPassword: TextInputEditText
    private lateinit var edtxtPasswordComprobacion: TextInputEditText

    private lateinit var txtDireccion: TextInputLayout
    private lateinit var txtTelefono: TextInputLayout
    private lateinit var txtPassword: TextInputLayout
    private lateinit var txtPasswordComprobacion: TextInputLayout

    private lateinit var edtxtNombre: TextInputEditText
    private lateinit var edtxtDni: TextInputEditText
    private lateinit var edtxtEmail: TextInputEditText
    private lateinit var rbNivelAfectuoso: RatingBar
    private lateinit var rbNivelAgresivo: RatingBar
    private lateinit var rbNivelEstricto: RatingBar
    private lateinit var rbNivelEnfermos: RatingBar
    private lateinit var tipoEmpleado: TextInputEditText

    private lateinit var btnGuardarCambios: Button

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edtxtNombre = view.findViewById(R.id.edtxtNombre)
        edtxtDni = view.findViewById(R.id.edtxtDni)
        edtxtEmail = view.findViewById(R.id.edtxtEmail)
        tipoEmpleado = view.findViewById(R.id.edtxtTipoEmpleado)
        rbNivelAfectuoso = view.findViewById(R.id.ratingBarNivelAfectuoso)
        rbNivelEstricto = view.findViewById(R.id.ratingBarNivelEstricto)
        rbNivelAgresivo = view.findViewById(R.id.ratingBarNivelAgresivo)
        rbNivelEnfermos = view.findViewById(R.id.ratingBarNivelEnfermos)
        edtxtDireccion = view.findViewById(R.id.edtxtDireccion)
        edtxtTelefono = view.findViewById(R.id.edtxtTelefono)
        edtxtPassword = view.findViewById(R.id.edtxtPassword)
        edtxtPasswordComprobacion = view.findViewById(R.id.edtxtPasswordComprobacion)

        txtDireccion = view.findViewById(R.id.txtDireccion)
        txtTelefono = view.findViewById(R.id.txtTelefono)
        txtPassword = view.findViewById(R.id.txtPassword)
        txtPasswordComprobacion = view.findViewById(R.id.txtPasswordComprobacion)

        edtxtDireccion.addTextChangedListener(crearTextWatcher(txtDireccion))
        edtxtTelefono.addTextChangedListener(crearTextWatcher(txtTelefono))
        edtxtPassword.addTextChangedListener(crearTextWatcher(txtPassword))
        edtxtPasswordComprobacion.addTextChangedListener(crearTextWatcher(txtPasswordComprobacion))

        btnGuardarCambios= view.findViewById(R.id.btnGuardarCambios)

        // Configura un OnClickListener para el botón
        btnGuardarCambios.setOnClickListener {
            // Llama al método registrar()
            guardarCambios()
        }

        mostrarDatosEmpleado(Login.empleado)
    }

    private fun mostrarDatosEmpleado(empleado: Empleado?) {
        edtxtNombre.setText(empleado!!.nombre)
        edtxtDni.setText(empleado.dni)
        edtxtEmail.setText(empleado.email)
        rbNivelAfectuoso.rating = empleado.nivelAfectuoso.toFloat()
        rbNivelAgresivo.rating = empleado.nivelAgresivo.toFloat()
        rbNivelEnfermos.rating = empleado.nivelEnfermos.toFloat()
        rbNivelEstricto.rating = empleado.nivelEstricto.toFloat()
        tipoEmpleado.setText(empleado.tipoEmpleado)
        edtxtDireccion.setText(empleado.direccion)
        edtxtTelefono.setText(empleado.telefono)
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

        if (edtxtPassword.text.isNullOrEmpty() && edtxtPassword.text!!.length < 6) {
            txtPassword.error = "La contraseña debe tener al menos 6 caracteres"
            esValido = false
        } else {
            txtPassword.error = null
        }

        if (edtxtPasswordComprobacion.text.isNullOrEmpty() && edtxtPasswordComprobacion.text.toString() != edtxtPassword.text.toString()) {
            txtPasswordComprobacion.error = "Las contraseñas no coinciden"
            esValido = false
        } else {
            txtPasswordComprobacion.error = null
        }

        if (edtxtTelefono.text.isNullOrEmpty() && (!Patterns.PHONE.matcher(edtxtTelefono.text!!).matches() || edtxtTelefono.text!!.length != 9)) {
            txtTelefono.error = "Introduce un número de teléfono válido"
            esValido = false
        } else {
            txtTelefono.error = null
        }

        return esValido
    }

    private fun guardarCambios() {

        if (validarCampos()) {
            val direccion: String = edtxtDireccion.text.toString().trim()
            val password: String = edtxtPassword.text.toString().trim()
            val telefono: String = edtxtTelefono.text.toString().trim()

            val url = "http://${Login.ip}/mascotasfelices/actualizarempleado.php"
            val requestQueue: RequestQueue = Volley.newRequestQueue(context)
            val resultadoPost = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.has("mensaje")) {
                        // Muestra un mensaje de error
                        Toast.makeText(
                            context,
                            "No se han podido guardar los cambios",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Login.empleado = Empleado(
                            dni = jsonResponse.getString("dni"),
                            nombre = jsonResponse.getString("nombre"),
                            direccion = if (jsonResponse.has("direccion") && !jsonResponse.isNull("direccion")) jsonResponse.getString(
                                "direccion"
                            ) else "",
                            email = jsonResponse.getString("email"),
                            tipoEmpleado = jsonResponse.getString("tipoEmpleado"),
                            telefono = if (jsonResponse.has("telefono") && !jsonResponse.isNull("telefono")) jsonResponse.getString(
                                "telefono"
                            ) else "",
                            nivelAfectuoso = jsonResponse.getInt("nivelAfectuoso"),
                            nivelAgresivo = jsonResponse.getInt("nivelAgresivo"),
                            nivelEstricto = jsonResponse.getInt("nivelEstricto"),
                            nivelEnfermos = jsonResponse.getInt("nivelEnfermos"),
                            residencia = jsonResponse.getInt("residencia"),
                            pagoHora = if (jsonResponse.has("pagoHora") && !jsonResponse.isNull("pagoHora")) jsonResponse.getDouble(
                                "pagoHora"
                            ).toFloat() else 0.0f,
                            salario = if (jsonResponse.has("salario") && !jsonResponse.isNull("salario")) jsonResponse.getDouble(
                                "salario"
                            ) else 0.0
                        )
                        Toast.makeText(context, "Cambios guardados con éxito", Toast.LENGTH_LONG).show()
                        mostrarDatosEmpleado(Login.empleado)
                    }
                }, Response.ErrorListener { _ ->
                    Toast.makeText(
                        context, "No se han podido guardar los cambios", Toast.LENGTH_LONG
                    ).show()
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val parametros = HashMap<String, String>()
                    parametros["dni"] = Login.empleado!!.dni
                    parametros["password"] = password
                    parametros["direccion"] = direccion
                    parametros["telefono"] = telefono
                    return parametros
                }
            }
            requestQueue.add(resultadoPost)
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PerfilAdmin.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PerfilAdmin().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}