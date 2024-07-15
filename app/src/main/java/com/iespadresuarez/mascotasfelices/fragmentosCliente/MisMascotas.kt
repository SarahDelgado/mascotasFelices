package com.iespadresuarez.mascotasfelices.fragmentosCliente

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.iespadresuarez.mascotasfelices.MascotaAdapter
import com.iespadresuarez.mascotasfelices.Login
import com.iespadresuarez.mascotasfelices.modelosBd.Mascota
import com.iespadresuarez.mascotasfelices.R
import java.time.LocalDate

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MisMascotas : Fragment(), MascotaAdapter.OnMascotaClickListener {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var mascotaAdapter: MascotaAdapter
    private lateinit var txtNoMascotas: TextView
    private lateinit var fabAddMascotas: FloatingActionButton

    private var listaMascotasOriginal = mutableListOf<Mascota>()
    private var listaMascotasFiltrada = mutableListOf<Mascota>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    /**
     * Método llamado cuando el fragmento se reanuda, para recargar la lista de mascotas.
     */
    override fun onResume() {
        super.onResume()

        obtenerListaMascotas { mascotas ->
            listaMascotasOriginal = mascotas.toMutableList()
            listaMascotasFiltrada = mascotas.toMutableList()
            mascotaAdapter.submitList(listaMascotasFiltrada)
        }/*
        obtenerListaMascotas { mascotas ->
            actualizarListaMascotas(mascotas)
        }*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mis_mascotas, container, false)

        txtNoMascotas = view.findViewById(R.id.txtNoMascotas)

        // Configuración del RecyclerView y adaptador
        recyclerView = view.findViewById(R.id.recyclerViewMascotas)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Inicializar el adaptador con la lista de mascotas vacía al inicio
        mascotaAdapter = MascotaAdapter(listaMascotasOriginal, this)
        recyclerView.adapter = mascotaAdapter

        obtenerListaMascotas { mascotas ->
            listaMascotasOriginal = mascotas.toMutableList()
            listaMascotasFiltrada = mascotas.toMutableList()
            mascotaAdapter.submitList(listaMascotasFiltrada)
        }
        /*
        // Obtener la lista de mascotas inicialmente
        obtenerListaMascotas { mascotas ->
            actualizarListaMascotas(mascotas)
        }*/
        fabAddMascotas = view.findViewById(R.id.fabAddMascotas)
        fabAddMascotas.setOnClickListener {
            irAddMascota()
        }
        return view
    }

    /**
     * Método para filtrar la lista de empleados desde el buscador
     */
    private fun filtrarMascotas(frase: String) {
        val listaFiltrada = if (frase.isEmpty()) {
            listaMascotasOriginal
        } else {
            val fraseMinuscula = frase.lowercase()
            listaMascotasOriginal.filter { it.nombre.lowercase().contains(fraseMinuscula) }
        }
        listaMascotasFiltrada.clear()
        listaMascotasFiltrada.addAll(listaFiltrada)
        mascotaAdapter.submitList(ArrayList(listaMascotasFiltrada))
    }

    /**
     * Implementación del método de la interfaz OnMascotaClickListener
     */
    override fun onMascotaClick(mascota: Mascota) {
        val intent =
            Intent(requireContext(), com.iespadresuarez.mascotasfelices.Mascota::class.java)
        intent.putExtra("codMascota", mascota.codMascota)
        startActivity(intent)
    }

    /**
     * Método para obtener la lista de mascotas
     */
    private fun obtenerListaMascotas(callback: (List<Mascota>) -> Unit) {

        val codCliente = Login.cliente?.codCliente
        val requestQueue = Volley.newRequestQueue(requireContext())
        val listaMascotasUrl =
            "http://${Login.ip}/mascotasfelices/listarmascotascliente.php?codCliente=$codCliente"
        val listaMascotasRequest = JsonArrayRequest(
            Request.Method.GET, listaMascotasUrl, null,
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
                // Llama al callback con la lista de mascotas obtenida
                callback(mascotas)
                // Actualiza la UI después de obtener las mascotas
                actualizarUI()
            },
            { _ ->
                // En caso de error, también actualiza la UI
                actualizarUI()
            }
        )
        requestQueue.add(listaMascotasRequest)
    }

    /**
     * Método para actualizar la interfaz de usuario dependiendo de si hay o no mascotas.
     */
    private fun actualizarUI() {
        if (listaMascotasOriginal.isNotEmpty()) {
            recyclerView.visibility = View.VISIBLE
            txtNoMascotas.visibility = View.GONE
        } else {
            recyclerView.visibility = View.GONE
            txtNoMascotas.visibility = View.VISIBLE
        }
    }
/*
    /**
     * Método para actualizar la lista de mascotas y notificar al adaptador.
     */
    private fun actualizarListaMascotas(mascotas: List<Mascota>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return listaMascotas.size
            }

            override fun getNewListSize(): Int {
                return mascotas.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return listaMascotas[oldItemPosition].codMascota == mascotas[newItemPosition].codMascota
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return listaMascotas[oldItemPosition] == mascotas[newItemPosition]
            }
        })

        listaMascotas.clear()
        listaMascotas.addAll(mascotas)
        diffResult.dispatchUpdatesTo(mascotaAdapter)
        actualizarUI()
        /*
        listaMascotas.clear()
        listaMascotas.addAll(mascotas)
        mascotaAdapter.notifyDataSetChanged()
        actualizarUI()*/
    }*/

    /**
     * Método para navegar a la pantalla de agregar mascota.
     */
    private fun irAddMascota() {
        val intent =
            Intent(requireContext(), com.iespadresuarez.mascotasfelices.Mascota::class.java)
        startActivity(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MisMascotas().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}

