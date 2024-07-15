package com.iespadresuarez.mascotasfelices.fragmentosCliente

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.iespadresuarez.mascotasfelices.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class InicioCliente : Fragment() {

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inicio_cliente, container, false)

        // TextView para abrir el fragmento de Paseos
        val txtPaseos = view.findViewById<TextView>(R.id.txtPaseos)
        txtPaseos.setOnClickListener {
            abrirFragmento(Paseos())
        }

        // TextView para abrir el fragmento de Alojamientos
        val txtAlojamientos = view.findViewById<TextView>(R.id.txtAlojamientos)
        txtAlojamientos.setOnClickListener {
            abrirFragmento(Hospedajes())
        }
        return view
    }

    private fun abrirFragmento(fragment: Fragment) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayoutCliente, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment IncioCliente.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InicioCliente().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}