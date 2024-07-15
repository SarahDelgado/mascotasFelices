package com.iespadresuarez.mascotasfelices.fragmentosAdmin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.iespadresuarez.mascotasfelices.Login
import com.iespadresuarez.mascotasfelices.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddEmpleado : Fragment() {

    private lateinit var edtxtDireccion: TextInputEditText
    private lateinit var edtxtTelefono: TextInputEditText
    private lateinit var edtxtPassword: TextInputEditText
    private lateinit var edtxtPasswordComprobacion: TextInputEditText
    private lateinit var edtxtNombre: TextInputEditText
    private lateinit var edtxtDni: TextInputEditText
    private lateinit var edtxtEmail: TextInputEditText
    private lateinit var sliderNivelAfectuoso: Slider
    private lateinit var sliderNivelAgresivo: Slider
    private lateinit var sliderNivelEstricto: Slider
    private lateinit var sliderNivelEnfermos: Slider
    private lateinit var edtxtSalario: TextInputEditText
    private lateinit var edtxtPagoHora: TextInputEditText
    private lateinit var tipoEmpleado: AutoCompleteTextView

    private lateinit var txtEmail: TextInputLayout
    private lateinit var txtPassword: TextInputLayout
    private lateinit var txtPasswordComprobacion: TextInputLayout
    private lateinit var txtNombre: TextInputLayout
    private lateinit var txtDni: TextInputLayout
    private lateinit var txtDireccion: TextInputLayout
    private lateinit var txtTelefono: TextInputLayout
    private lateinit var txtSalario: TextInputLayout
    private lateinit var txtPagoHora: TextInputLayout
    private lateinit var txtTipoEmpleado: TextInputLayout

    private var valorSliderNivelAfectuoso = "0"
    private var valorSliderNivelAgresivo = "0"
    private var valorSliderNivelEstricto = "0"
    private var valorSliderNivelEnfermos = "0"

    private lateinit var btnAddEmpleado: Button

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
        return inflater.inflate(R.layout.fragment_add_empleado, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edtxtNombre = view.findViewById(R.id.edtxtNombre)
        edtxtDni = view.findViewById(R.id.edtxtDni)
        edtxtEmail = view.findViewById(R.id.edtxtEmail)
        sliderNivelAfectuoso = view.findViewById(R.id.sliderNivelAfectuoso)
        sliderNivelEstricto = view.findViewById(R.id.sliderNivelEstricto)
        sliderNivelAgresivo = view.findViewById(R.id.sliderNivelAgresivo)
        sliderNivelEnfermos = view.findViewById(R.id.sliderNivelEnfermos)

        edtxtSalario = view.findViewById(R.id.edtxtSalario)
        edtxtPagoHora = view.findViewById(R.id.edtxtPagoHora)
        tipoEmpleado = view.findViewById(R.id.autocompleteTipoEmpleado)
        edtxtDireccion = view.findViewById(R.id.edtxtDireccion)
        edtxtTelefono = view.findViewById(R.id.edtxtTelefono)
        edtxtPassword = view.findViewById(R.id.edtxtPassword)
        edtxtPasswordComprobacion = view.findViewById(R.id.edtxtPasswordComprobacion)

        btnAddEmpleado = view.findViewById(R.id.btnAddEmpleado)
        // Configura un OnClickListener para el botón
        btnAddEmpleado.setOnClickListener {
            // Llama al método registrar()
            addEmpleado()
        }

        // Configurar el dropdown del tipo de empleado
        val tiposEmpleados = resources.getStringArray(R.array.tiposEmpleado)
        val adapter =
            ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, tiposEmpleados)
        tipoEmpleado.setAdapter(adapter)

        // Listener para detectar cambios en el tipo de empleado
        tipoEmpleado.setOnItemClickListener { parent, _, position, _ ->
            val selectedTipo = parent.getItemAtPosition(position) as String
            actualizarCamposVisibles(selectedTipo)
        }

        sliderNivelAfectuoso.addOnChangeListener { _, value, _ ->
            valorSliderNivelAfectuoso = value.toString()
        }
        sliderNivelAgresivo.addOnChangeListener { _, value, _ ->
            valorSliderNivelAgresivo = value.toString()
        }
        sliderNivelEstricto.addOnChangeListener { _, value, _ ->
            valorSliderNivelEstricto = value.toString()
        }
        sliderNivelEnfermos.addOnChangeListener { _, value, _ ->
            valorSliderNivelEnfermos = value.toString()
        }

        txtEmail = view.findViewById(R.id.txtEmail)
        txtPassword = view.findViewById(R.id.txtPassword)
        txtPasswordComprobacion = view.findViewById(R.id.txtPasswordComprobacion)
        txtNombre = view.findViewById(R.id.txtNombre)
        txtDireccion = view.findViewById(R.id.txtDireccion)
        txtTelefono = view.findViewById(R.id.txtTelefono)
        txtDni = view.findViewById(R.id.txtDni)
        txtSalario = view.findViewById(R.id.txtSalario)
        txtPagoHora = view.findViewById(R.id.txtPagoHora)
        txtTipoEmpleado = view.findViewById(R.id.txtTipoEmpleado)

        edtxtEmail.addTextChangedListener(crearTextWatcher(txtEmail))
        edtxtPassword.addTextChangedListener(crearTextWatcher(txtPassword))
        edtxtPasswordComprobacion.addTextChangedListener(crearTextWatcher(txtPasswordComprobacion))
        edtxtNombre.addTextChangedListener(crearTextWatcher(txtNombre))
        edtxtDireccion.addTextChangedListener(crearTextWatcher(txtDireccion))
        edtxtTelefono.addTextChangedListener(crearTextWatcher(txtTelefono))
        edtxtDni.addTextChangedListener(crearTextWatcher(txtDni))
        edtxtSalario.addTextChangedListener(crearTextWatcher(txtSalario))
        edtxtPagoHora.addTextChangedListener(crearTextWatcher(txtPagoHora))
        tipoEmpleado.addTextChangedListener(crearTextWatcher(txtTipoEmpleado))

    }

    private fun actualizarCamposVisibles(tipo: String) {
        when (tipo) {
            getString(R.string.cuidador) -> {
                txtSalario.visibility = View.VISIBLE
                txtPagoHora.visibility = View.GONE
            }

            getString(R.string.paseador) -> {
                txtSalario.visibility = View.GONE
                txtPagoHora.visibility = View.VISIBLE
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

        if (edtxtEmail.text.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(edtxtEmail.text!!)
                .matches()
        ) {
            txtEmail.error = "Introduce un correo electrónico válido"
            esValido = false
        } else {
            edtxtEmail.error = null
        }

        if (edtxtDni.text.isNullOrEmpty() || edtxtDni.text!!.length != 9) {
            edtxtDni.error = "Introduce un DNI válido"
            esValido = false
        } else {
            edtxtDni.error = null
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
            txtNombre.error = "Introduce un nombre"
            esValido = false
        } else {
            edtxtNombre.error = null
        }

        if (edtxtDireccion.text.isNullOrEmpty()) {
            txtDireccion.error = "Introduce una dirección"
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

        if (tipoEmpleado.text.isNullOrEmpty()) {
            tipoEmpleado.error = "Introduceun tipo de empleado"
            esValido = false
        } else {
            tipoEmpleado.error = null
        }

        if (tipoEmpleado.text.toString() == getString(R.string.cuidador)) {
            if (edtxtSalario.text.isNullOrEmpty()) {
                edtxtSalario.error = "Introduce el salario mensual del cuidador"
                esValido = false
            } else {
                edtxtSalario.error = null
            }
        } else if (tipoEmpleado.text.toString() == getString(R.string.paseador)) {
            if (edtxtPagoHora.text.isNullOrEmpty()) {
                edtxtPagoHora.error = "Introduce el pago por hora del paseador"
                esValido = false
            } else {
                edtxtPagoHora.error = null
            }
        }

        return esValido
    }

    private fun addEmpleado() {

        if (validarCampos()) {

            val dni: String = edtxtDni.text.toString().trim()
            val nombre: String = edtxtNombre.text.toString().trim()
            val direccion: String = edtxtDireccion.text.toString().trim()
            val email: String = edtxtEmail.text.toString().trim()
            val password: String = edtxtPassword.text.toString().trim()
            val telefono: String = edtxtTelefono.text.toString().trim()
            val nivelAfectuoso: String = valorSliderNivelAfectuoso
            val nivelAgresivo: String = valorSliderNivelAgresivo
            val nivelEsctricto: String = valorSliderNivelEstricto
            val nivelEnfermos: String = valorSliderNivelEnfermos
            val tipoEmpleadoSeleccionado: String = tipoEmpleado.text.toString().trim()
            val salario = edtxtSalario.text.toString()
            val pagoHora = edtxtPagoHora.text.toString()

            val tipoEmpleadoClave: String = when (tipoEmpleadoSeleccionado) {
                getString(R.string.paseador) -> "Paseador"
                getString(R.string.cuidador) -> "Cuidador"
                else -> tipoEmpleadoSeleccionado
            }

            val url = "http://${Login.ip}/mascotasfelices/insertarempleado.php"
            val requestQueue: RequestQueue = Volley.newRequestQueue(context)
            val resultadoPost = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    if (response.trim() == "success") {
                        Toast.makeText(
                            requireContext(),
                            "Empleado añadido con éxito",
                            Toast.LENGTH_LONG
                        ).show()
                        // Registro exitoso, limpia los campos
                        limpiarCampos()
                    } else if (response.trim() == "error") {
                        Toast.makeText(
                            context,
                            "No se ha podido añadir al empleado",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }, Response.ErrorListener { _ ->
                    Toast.makeText(
                        context,
                        "No se ha podido añadir al empleado",
                        Toast.LENGTH_LONG
                    ).show()
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val parametros = HashMap<String, String>()
                    parametros["email"] = email
                    parametros["password"] = password
                    parametros["nombre"] = nombre
                    parametros["direccion"] = direccion
                    parametros["dni"] = dni
                    parametros["telefono"] = telefono
                    parametros["nivelAfectuoso"] = nivelAfectuoso
                    parametros["nivelAgresivo"] = nivelAgresivo
                    parametros["nivelEstricto"] = nivelEsctricto
                    parametros["nivelEnfermos"] = nivelEnfermos
                    parametros["residencia"] = Login.empleado!!.residencia.toString()
                    parametros["tipoEmpleado"] = tipoEmpleadoClave
                    when (tipoEmpleadoClave) {
                        getString(R.string.paseador) -> {
                            parametros["paseoHora"] = pagoHora
                            parametros["salario"] = ""
                        }

                        getString(R.string.cuidador) -> {
                            parametros["paseoHora"] = ""
                            parametros["salario"] = salario
                        }
                    }
                    return parametros
                }
            }
            requestQueue.add(resultadoPost)
        }
    }

    private fun limpiarCampos() {
        edtxtNombre.text?.clear()
        edtxtDni.text?.clear()
        edtxtEmail.text?.clear()
        edtxtDireccion.text?.clear()
        edtxtTelefono.text?.clear()
        edtxtPassword.text?.clear()
        edtxtPasswordComprobacion.text?.clear()
        edtxtSalario.text?.clear()
        edtxtPagoHora.text?.clear()
        tipoEmpleado.setText("")
        sliderNivelAfectuoso.value = 0f
        sliderNivelAgresivo.value = 0f
        sliderNivelEstricto.value = 0f
        sliderNivelEnfermos.value = 0f
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddEmpleado.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddEmpleado().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}