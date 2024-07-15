package com.iespadresuarez.mascotasfelices.fragmentosCliente

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.iespadresuarez.mascotasfelices.Login
import com.iespadresuarez.mascotasfelices.R
import com.iespadresuarez.mascotasfelices.modelosBd.Cliente
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PerfilCliente.newInstance] factory method to
 * create an instance of this fragment.
 */
class PerfilCliente : Fragment() {

    private lateinit var edtxtNombre: TextInputEditText
    private lateinit var edtxtDireccion: TextInputEditText
    private lateinit var edtxtEmail: TextInputEditText
    private lateinit var edtxtPassword: TextInputEditText
    private lateinit var edtxtPasswordComprobacion: TextInputEditText
    private lateinit var edtxtTelefono1: TextInputEditText
    private lateinit var edtxtTelefono2: TextInputEditText

    private lateinit var txtPassword: TextInputLayout
    private lateinit var txtPasswordComprobacion: TextInputLayout
    private lateinit var txtDireccion: TextInputLayout
    private lateinit var txtTelefono1: TextInputLayout
    private lateinit var txtTelefono2: TextInputLayout

    private lateinit var btnGuardarCambios: Button

    // TODO: Rename and change types of parameters
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
        return inflater.inflate(R.layout.fragment_perfil_cliente, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edtxtNombre = view.findViewById(R.id.edtxtNombre)
        edtxtEmail = view.findViewById(R.id.edtxtEmail)
        edtxtDireccion = view.findViewById(R.id.edtxtDireccion)
        edtxtTelefono1 = view.findViewById(R.id.edtxtTelefono1)
        edtxtTelefono2 = view.findViewById(R.id.edtxtTelefono2)
        edtxtPassword = view.findViewById(R.id.edtxtPassword)
        edtxtPasswordComprobacion = view.findViewById(R.id.edtxtPasswordComprobacion)

        txtPassword = view.findViewById(R.id.txtPassword)
        txtPasswordComprobacion = view.findViewById(R.id.txtPasswordComprobacion)
        txtDireccion = view.findViewById(R.id.txtDireccion)
        txtTelefono1 = view.findViewById(R.id.txtTelefono1)
        txtTelefono2 = view.findViewById(R.id.txtTelefono2)

        edtxtPassword.addTextChangedListener(crearTextWatcher(txtPassword))
        edtxtPasswordComprobacion.addTextChangedListener(crearTextWatcher(txtPasswordComprobacion))
        edtxtDireccion.addTextChangedListener(crearTextWatcher(txtDireccion))
        edtxtTelefono1.addTextChangedListener(crearTextWatcher(txtTelefono1))
        edtxtTelefono2.addTextChangedListener(crearTextWatcher(txtTelefono2))

        btnGuardarCambios = view.findViewById(R.id.btnGuardarCambios)
        // Configura un OnClickListener para el botón
        btnGuardarCambios.setOnClickListener {
            // Llama al método registrar()
            guardarCambios()
        }

        mostrarDatosCliente(Login.cliente)
    }

    private fun mostrarDatosCliente(cliente: Cliente?) {
        edtxtNombre.setText(cliente!!.nombre)
        edtxtEmail.setText(cliente.email)
        edtxtDireccion.setText(cliente.direccion)
        edtxtTelefono1.setText(cliente.telefono1)
        edtxtTelefono2.setText(cliente.telefono2)
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

        if (!edtxtPassword.text.isNullOrEmpty() && edtxtPassword.text!!.length < 6) {
            edtxtPassword.error = "La contraseña debe tener al menos 6 caracteres"
            esValido = false
        } else {
            edtxtPassword.error = null
        }

        if (!edtxtPasswordComprobacion.text.isNullOrEmpty() && edtxtPasswordComprobacion.text.toString() != edtxtPassword.text.toString()) {
            edtxtPasswordComprobacion.error = "Las contraseñas no coinciden"
            esValido = false
        } else {
            edtxtPasswordComprobacion.error = null
        }

        if (!edtxtTelefono1.text.isNullOrEmpty() && (!Patterns.PHONE.matcher(edtxtTelefono1.text!!)
                .matches() || edtxtTelefono1.text!!.length != 9)
        ) {
            edtxtTelefono1.error = "Introduce un número de teléfono válido"
            esValido = false
        } else {
            edtxtTelefono1.error = null
        }

        if (!edtxtTelefono2.text.isNullOrEmpty() && (!Patterns.PHONE.matcher(edtxtTelefono2.text!!)
                .matches() || edtxtTelefono2.text!!.length != 9)
        ) {
            edtxtTelefono2.error = "Introduce un número de teléfono válido"
            esValido = false
        } else {
            edtxtTelefono2.error = null
        }

        return esValido
    }

    private fun guardarCambios() {

        if (validarCampos()) {
            val direccion: String = edtxtDireccion.text.toString().trim()
            val password: String = edtxtPassword.text.toString().trim()
            val telefono1: String = edtxtTelefono1.text.toString().trim()
            val telefono2: String = edtxtTelefono2.text.toString().trim()

            val url = "http://${Login.ip}/mascotasfelices/actualizarcliente.php"
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
                        Login.cliente = Cliente(
                            codCliente = jsonResponse.getInt("codCliente"),
                            email = jsonResponse.getString("email"),
                            nombre = jsonResponse.getString("nombre"),
                            direccion = if (jsonResponse.has("direccion") && !jsonResponse.isNull("direccion")) jsonResponse.getString(
                                "direccion"
                            ) else "",
                            telefono1 = if (jsonResponse.has("telefono1") && !jsonResponse.isNull("telefono1")) jsonResponse.getString(
                                "telefono1"
                            ) else "",
                            telefono2 = if (jsonResponse.has("telefono2") && !jsonResponse.isNull("telefono2")) jsonResponse.getString(
                                "telefono2"
                            ) else ""
                        )
                        Toast.makeText(context, "Cambios guardados con éxito", Toast.LENGTH_LONG).show()
                        mostrarDatosCliente(Login.cliente)
                    }
                }, Response.ErrorListener { _ ->
                    Toast.makeText(context, "No se han podido guardar los cambios", Toast.LENGTH_LONG).show()
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val parametros = HashMap<String, String>()
                    parametros["codCliente"] = Login.cliente!!.codCliente.toString()
                    parametros["password"] = password
                    parametros["direccion"] = direccion
                    parametros["telefono1"] = telefono1
                    parametros["telefono2"] = telefono2
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
         * @return A new instance of fragment PerfilCliente.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PerfilCliente().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}