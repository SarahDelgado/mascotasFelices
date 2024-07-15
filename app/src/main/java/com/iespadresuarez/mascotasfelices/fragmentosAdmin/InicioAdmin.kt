package com.iespadresuarez.mascotasfelices.fragmentosAdmin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.iespadresuarez.mascotasfelices.EmpleadoAdapter
import com.iespadresuarez.mascotasfelices.Login
import com.iespadresuarez.mascotasfelices.R
import com.iespadresuarez.mascotasfelices.modelosBd.Empleado

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class InicioAdmin : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var empleadoAdapter: EmpleadoAdapter
    private lateinit var fabAddEmpleado: FloatingActionButton
    private lateinit var fabBorrarEmpleado: FloatingActionButton
    private lateinit var searchView: SearchView

    private var listaEmpleadosOriginal = mutableListOf<Empleado>()
    private var listaEmpleadosFiltrada = mutableListOf<Empleado>()

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onResume() {
        super.onResume()
        obtenerListaEmpleados { listaEmpleados ->
            listaEmpleadosOriginal = listaEmpleados.toMutableList()
            listaEmpleadosFiltrada = listaEmpleados.toMutableList()
            empleadoAdapter.submitList(listaEmpleadosFiltrada)

            filtrarEmpleados(searchView.query.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inicio_admin, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewEmpleados)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inicialización del adaptador
        empleadoAdapter = EmpleadoAdapter { empleado ->
            irAddHorario(empleado.dni, empleado.nombre, empleado.tipoEmpleado)
        }
        recyclerView.adapter = empleadoAdapter

        // Obtiene la lista de empleados
        obtenerListaEmpleados { listaEmpleados ->
            listaEmpleadosOriginal = listaEmpleados.toMutableList()
            listaEmpleadosFiltrada = listaEmpleados.toMutableList()
            empleadoAdapter.submitList(listaEmpleadosFiltrada)
        }

        searchView = view.findViewById(R.id.searchViewEmpleados)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarEmpleados(newText.orEmpty())
                return true
            }
        })

        fabAddEmpleado = view.findViewById(R.id.fabAddEmpleado)
        fabAddEmpleado.setOnClickListener {
            irAddEmpleado()
        }

        fabBorrarEmpleado = view.findViewById(R.id.fabBorrarEmpleado)
        fabBorrarEmpleado.setOnClickListener {
            irBorrarEmpleado()
        }

        return view
    }

    /**
     * Método para filtrar la lista de empleados desde el buscador
     */
    private fun filtrarEmpleados(frase: String) {
        val listaFiltrada = if (frase.isEmpty()) {
            listaEmpleadosOriginal
        } else {
            val fraseMinuscula = frase.lowercase()
            listaEmpleadosOriginal.filter { it.nombre.lowercase().contains(fraseMinuscula) }
        }
        listaEmpleadosFiltrada.clear()
        listaEmpleadosFiltrada.addAll(listaFiltrada)
        empleadoAdapter.submitList(ArrayList(listaEmpleadosFiltrada))
    }

    /**
     * Método para obtener la lista de empleados
     */
    private fun obtenerListaEmpleados(callback: (List<Empleado>) -> Unit) {
        val requestQueue = Volley.newRequestQueue(requireContext())
        val residencia = Login.empleado?.residencia

        val listaEmpeladosUrl =
            "http://${Login.ip}/mascotasfelices/listarempleados.php?residencia=$residencia"
        val listaEmpeladosRequest = JsonArrayRequest(
            Request.Method.GET, listaEmpeladosUrl, null,
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

    /**
     * Método para abrir el fragmento para añadir empleados
     */
    private fun irAddEmpleado() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.frameLayoutAdmin, AddEmpleado())
            ?.addToBackStack(null)
            ?.commit()
    }

    /**
     * Método para abrir el fragmento de añadir horario al empleado
     */
    private fun irAddHorario(dni: String, nombre: String, tipoEmpleado: String) {
        val fragment = AddHorario.newInstance(dni, nombre, tipoEmpleado)
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.frameLayoutAdmin, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    /**
     * Método para abrir el fragmento de borrar empleado
     */
    private fun irBorrarEmpleado() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.frameLayoutAdmin, BorrarEmpleado())
            ?.addToBackStack(null)
            ?.commit()
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
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InicioAdmin().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}