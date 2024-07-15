package com.iespadresuarez.mascotasfelices.fragmentosAdmin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.iespadresuarez.mascotasfelices.Login
import com.iespadresuarez.mascotasfelices.R
import com.iespadresuarez.mascotasfelices.modelosBd.Empleado


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BorrarEmpleado : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var autoCompleteEmpleados: MaterialAutoCompleteTextView
    private lateinit var txtEmpleado: TextInputLayout
    private lateinit var btnBorrarEmpleado: Button

    private lateinit var listaEmpleados: MutableList<Empleado>
    private lateinit var adapter: ArrayAdapter<String>

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
        val view = inflater.inflate(R.layout.fragment_borrar_empleado, container, false)

        // Inicialización de vistas
        autoCompleteEmpleados = view.findViewById(R.id.autocompleteEmpleado)
        txtEmpleado = view.findViewById(R.id.txtEmpleado)
        btnBorrarEmpleado = view.findViewById(R.id.btnBorrarEmpleado)

        // Obtiene la lista de empleados
        obtenerListaEmpleados { empleados ->
            listaEmpleados = empleados.toMutableList()
            val nombresEmpleados = empleados.map { it.nombre }
            adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                nombresEmpleados
            )
            autoCompleteEmpleados.setAdapter(adapter)
        }

        // Manejar clic en el botón de borrar
        btnBorrarEmpleado.setOnClickListener {
            borrar()
        }

        return view
    }

    /**
     * Método para obtener la lista de empleados
     */
    private fun obtenerListaEmpleados(callback: (List<Empleado>) -> Unit) {

        val residencia = Login.empleado?.residencia
        val requestQueue = Volley.newRequestQueue(requireContext())
        val listaEmpleadosUrl = "http://${Login.ip}/mascotasfelices/listarempleados.php?residencia=$residencia"
        val listaEmpeladosRequest = JsonArrayRequest(
            Request.Method.GET, listaEmpleadosUrl, null,
            { response ->
                val listaEmpleados = mutableListOf<Empleado>()
                for (i in 0 until response.length()) {
                    val empleadoObject = response.getJSONObject(i)
                    val empleado = Empleado(
                        dni = empleadoObject.getString("dni"),
                        nombre = empleadoObject.getString("nombre"),
                        direccion = empleadoObject.getString("direccion"),
                        email = empleadoObject.getString("email"),
                        tipoEmpleado = empleadoObject.getString("tipoEmpleado"),
                        telefono = empleadoObject.getString("telefono"),
                        nivelAfectuoso = empleadoObject.getInt("nivelAfectuoso"),
                        nivelAgresivo = empleadoObject.getInt("nivelAgresivo"),
                        nivelEstricto = empleadoObject.getInt("nivelEstricto"),
                        nivelEnfermos = empleadoObject.getInt("nivelEnfermos"),
                        residencia = empleadoObject.getInt("residencia"),
                        pagoHora = if (!empleadoObject.isNull("pagoHora")) empleadoObject.getDouble(
                            "pagoHora"
                        ).toFloat() else 0.0f,
                        salario = if (!empleadoObject.isNull("salario")) empleadoObject.getDouble("salario") else 0.0
                    )
                    listaEmpleados.add(empleado)
                }
                callback(listaEmpleados)
            },
            { _ ->
                Toast.makeText(
                    requireContext(),
                    "Error al obtener la lista de empleados",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
        requestQueue.add(listaEmpeladosRequest)
    }

    private fun validarCampos(): Boolean {
        var esValido = true

        if (autoCompleteEmpleados.text.isNullOrEmpty()) {
            txtEmpleado.error = "Seleccione un empleado"
            esValido = false
        } else {
            txtEmpleado.error = null
        }

        return esValido
    }

    private fun borrar(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.borrarEmpleado))
            .setMessage(resources.getString(R.string.seguroBorrarEmpleado))
            .setNegativeButton(resources.getString(R.string.cancelar)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.Aceptar)) { dialog, which ->
                borrarEmpleadoSeleccionado()
            }
            .show()
    }

    private fun borrarEmpleadoSeleccionado() {

        val nombreEmpleado = autoCompleteEmpleados.text.toString()
        val empleadoSeleccionado = listaEmpleados.find { it.nombre == nombreEmpleado }

        if (validarCampos()) {
            val requestQueue = Volley.newRequestQueue(requireContext())
            val url = "http://${Login.ip}/mascotasfelices/borrarempleado.php"
            val resultadoPost = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    if (response.trim() == "success") {
                        Toast.makeText(
                            requireContext(),
                            "Empleado borrado con éxito",
                            Toast.LENGTH_LONG
                        ).show()
                        // Remover el empleado de la lista y actualizar el adaptador
                        listaEmpleados.remove(empleadoSeleccionado)
                        val nombresEmpleados = listaEmpleados.map { it.nombre }
                        adapter.clear()
                        adapter.addAll(nombresEmpleados)
                        autoCompleteEmpleados.setText("")
                    } else if (response.trim() == "error") {
                        Toast.makeText(
                            context,
                            "No se ha podido borrar al empleado",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                { _ ->
                    Toast.makeText(
                        requireContext(),
                        "Error al borrar al empleado",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val parametros = HashMap<String, String>()
                    parametros["dni"] = empleadoSeleccionado!!.dni
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
         * @return A new instance of fragment BorrarEmpleado.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BorrarEmpleado().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}