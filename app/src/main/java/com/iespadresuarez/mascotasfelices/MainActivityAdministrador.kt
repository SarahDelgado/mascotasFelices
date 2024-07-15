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
import com.iespadresuarez.mascotasfelices.fragmentosAdmin.AddEmpleado
import com.iespadresuarez.mascotasfelices.fragmentosAdmin.BorrarEmpleado
import com.iespadresuarez.mascotasfelices.fragmentosAdmin.InicioAdmin
import com.iespadresuarez.mascotasfelices.fragmentosAdmin.PerfilAdmin

/**
 * Actividad principal para el administrador, que gestiona fragmentos y menú de navegación.
 */
class MainActivityAdministrador : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout

    /**
     * Método llamado cuando se crea la actividad.
     * @param savedInstanceState Estado previamente guardado de la actividad, si existe.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_administrador)
        // Si no hay estado guardado previo, reemplaza el fragmento inicial por el fragmento de InicioAdmin.
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayoutAdmin, InicioAdmin())
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
        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_admin)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.drawerLayoutAdmin)
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    /**
     * Inicializa el menú lateral (NavigationView) y configura el listener para los elementos de menú.
     */
    private fun incializarMenu() {
        val navigationView : NavigationView = findViewById(R.id.navigationViewAdmin)
        navigationView.setNavigationItemSelectedListener (this)
        // Configura el nombre de usuario en el encabezado del menú lateral.
        val headerView: View = navigationView.getHeaderView(0)
        val txtNombreUsuario: TextView = headerView.findViewById(R.id.txtNombreUsuario)
        txtNombreUsuario.text = Login.empleado?.nombre
    }

    /**
     * Reemplaza el fragmento principal en el contenedor (FrameLayout) por el fragmento correspondiente al elemento de menú seleccionado.
     * @param item Elemento de menú seleccionado.
     * @return true si el fragmento se reemplaza correctamente, false de lo contrario.
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var ok : Boolean = false
        when (item.itemId) {
            R.id.menuItemInicioAdmin -> {
                reemplazarFragmento(InicioAdmin())
                ok = true
            }
            R.id.menuItemPerfilAdmin -> {
                reemplazarFragmento(PerfilAdmin())
                ok = true
            }
            R.id.menuItemAddEmpleado -> {
                reemplazarFragmento(AddEmpleado())
                ok = true
            }
            R.id.menuItemBorrarEmpleado -> {
                reemplazarFragmento(BorrarEmpleado())
                ok = true
            }
            R.id.menuItemCerrarSesionAdmin -> {
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
        fragmentTransaction.replace(R.id.frameLayoutAdmin, fragmento)
        fragmentTransaction.commit()
    }

    /**
     * Cierra la sesión actual del empleado y redirige a la actividad de inicio de sesión (Login).
     */
    private fun cerrarSesion() {
        Login.empleado = null
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }

}