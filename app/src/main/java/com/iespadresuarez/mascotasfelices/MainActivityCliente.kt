package com.iespadresuarez.mascotasfelices

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.iespadresuarez.mascotasfelices.fragmentosCliente.Historial
import com.iespadresuarez.mascotasfelices.fragmentosCliente.MisMascotas
import com.iespadresuarez.mascotasfelices.fragmentosCliente.PerfilCliente
import com.iespadresuarez.mascotasfelices.fragmentosCliente.InicioCliente

/**
 * Actividad principal para el cliente, que gestiona fragmentos y menú de navegación.
 */
class MainActivityCliente: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout

    /**
     * Método llamado cuando se crea la actividad.
     * @param savedInstanceState Estado previamente guardado de la actividad, si existe.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_cliente)
        // Si no hay estado guardado previo, reemplaza el fragmento inicial por el fragmento de InicioCliente.
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayoutCliente, InicioCliente())
                .commit()
        }

        // Inicializa la barra de herramientas (Toolbar) y el menú lateral (NavigationView).
        inicializarToolbar()
        incializarMenu()
    }

    /**
     * Inicializa la barra de herramientas (Toolbar) y establece el ActionBarDrawerToggle para el menú lateral.
     */
    private fun inicializarToolbar() {
        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_cliente)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawerLayoutCliente)
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    /**
     * Inicializa el menú lateral (NavigationView) y configura el listener para los elementos de menú.
     */
    private fun incializarMenu() {
        val navigationView : NavigationView = findViewById(R.id.navigationViewCliente)
        navigationView.setNavigationItemSelectedListener (this)
        // Configura el nombre de usuario en el encabezado del menú lateral.
        val headerView: View = navigationView.getHeaderView(0)
        val txtNombreUsuario: TextView = headerView.findViewById(R.id.txtNombreUsuario)
        txtNombreUsuario.text = Login.cliente?.nombre
    }

    /**
     * Reemplaza el fragmento principal en el contenedor (FrameLayout) por el fragmento correspondiente al elemento de menú seleccionado.
     * @param item Elemento de menú seleccionado.
     * @return true si el fragmento se reemplaza correctamente, false de lo contrario.
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var ok : Boolean = false
        when (item.itemId) {
            R.id.menuItemInicioCliente -> {
                reemplazarFragmento(InicioCliente())
                ok = true
            }
            R.id.menuItemPerfilCliente -> {
                reemplazarFragmento(PerfilCliente())
                ok = true
            }
            R.id.menuItemMascotas -> {
                reemplazarFragmento(MisMascotas())
                ok = true
            }
            R.id.menuItemHistorial -> {
                reemplazarFragmento(Historial())
                ok = true
            }
            R.id.menuItemCerrarSesionCliente -> {
                cerrarSesion()
                ok = true
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return ok
    }

    /**
     * Reemplaza el fragmento actual en el contenedor por el fragmento especificado.
     * @param fragmento Fragmento que se va a mostrar.
     */
    private fun reemplazarFragmento(fragmento: androidx.fragment.app.Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayoutCliente, fragmento)
        fragmentTransaction.commit()
    }

    /**
     * Cierra la sesión actual del cliente y redirige a la actividad de inicio de sesión (Login).
     */
    private fun cerrarSesion() {
        Login.cliente = null
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }


}