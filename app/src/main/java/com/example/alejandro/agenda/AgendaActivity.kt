package com.example.alejandro.agenda

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log

import android.widget.*
import org.json.JSONArray


class AgendaActivity : AppCompatActivity(), DataPassListener, DataBaseListener {



    private var manager: FragmentManager? = null
    private var agendaDB: AgendaBaseDatos? = null
    private var ident: IntArray? = null
    private var contactos: ArrayList<Contacto> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)
        comprobarPermisos()
        agendaDB = AgendaBaseDatos(this)
        manager = supportFragmentManager

        val transaction = manager!!.beginTransaction()
        val fragmentLista = AgendaFragment()

        transaction.add(R.id.agendaActivityLayout, fragmentLista, "fragmentPrincipal")
        transaction.addToBackStack(null)
        transaction.commit()


    }


    override fun passData(data: Bundle, fragment: Int) {
        val transaction = manager!!.beginTransaction()
        val fragmentReplace: Fragment
            when (fragment) {
                0 -> {
                    fragmentReplace = if( manager!!.findFragmentByTag("\"fragmentPrincipal\"") != null) {
                        manager!!.findFragmentByTag("\"fragmentPrincipal\"") as AgendaFragment
                    }else{
                        AgendaFragment()
                    }

                    fragmentReplace.arguments = data
                    transaction.replace(R.id.agendaActivityLayout, fragmentReplace, "fragmentPrincipal")
                    transaction.addToBackStack(null)

                }
                1 -> {

                    fragmentReplace = if( manager!!.findFragmentByTag("\"fragmentCrear\"") != null) {
                        manager!!.findFragmentByTag("\"fragmentCrear\"") as CrearContactoFragment
                    }else{
                        CrearContactoFragment()
                    }

                    fragmentReplace.arguments = data
                    transaction.replace(R.id.agendaActivityLayout, fragmentReplace, "fragmentCrear")
                    transaction.addToBackStack(null)

                }
                else -> {


                    fragmentReplace = if( manager!!.findFragmentByTag("\"fragmentMostrar\"") != null) {
                        manager!!.findFragmentByTag("\"fragmentMostrar\"") as MostrarContactoFragment
                    }else{
                        MostrarContactoFragment()
                    }

                    fragmentReplace.arguments = data

                    transaction.replace(R.id.agendaActivityLayout, fragmentReplace, "fragmentMostrar")
                    transaction.addToBackStack(null)


                }
            }
        transaction.commit()
    }

    override fun modifiedDataContact(id: Int, nombre: String, direccion: String, movil: String, telefono: String, correo: String) {
        agendaDB!!.modificarContacto(id, nombre, direccion, movil, telefono, correo)
    }

    override fun returnIdentContact(): Pair<IntArray?, ArrayList<Contacto>> {
        val numFilas = agendaDB!!.numerodeFilas()
        contactos.clear()
        ident = if (numFilas > 0) {
            agendaDB!!.recuperaIds()
        }else{
            null
        }


        if (ident != null){
            for (i in 0 until ident!!.size){
                contactos.add(agendaDB!!.buscarContacto(ident!![i]))
            }
        }
        return Pair(ident, contactos)
    }

    override fun searchDataContact(id: Int): Contacto {
        return agendaDB!!.buscarContacto(id)
    }

    override fun createContact(nombre: String, direccion: String, movil: String, telefono: String, correo: String) {
        agendaDB!!.insertarContacto(nombre, direccion, movil, telefono, correo)
    }

    override fun deleteContact(id: Int) {
        agendaDB!!.borrarContacto(id)
    }

    override fun getJsonData(): JSONArray {
        return agendaDB!!.getJson()
    }

    override fun databaseInstance(): AgendaBaseDatos {
        return agendaDB!!
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        val t1: Toast


        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE ->

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    t1 = Toast.makeText(this, "permisos de escritura concedidios", Toast.LENGTH_LONG)
                    t1.show()
                } else {
                    val t = Toast.makeText(this, "No se han concedido los permisos necesarios", Toast.LENGTH_LONG)
                    t.show()
                    comprobarPermisos()
                }
        }
    }

    internal fun comprobarPermisos() {
        val permisos = arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Toast.makeText(this, "This version is not Android 6 or later " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show()
        } else {
            if ((ContextCompat.checkSelfPermission(this,
                            permisos[1]) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this,
                            permisos[2]) != PackageManager.PERMISSION_GRANTED) ){
                when {
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            permisos[1]) -> showSnackBar("escritura en SD")
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            permisos[2]) -> showSnackBar("callPhone")
                    else -> requestPermissions(
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE), MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
                }
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(permisos[1], permisos[2]), MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun showSnackBar(texto: String) {
        Snackbar.make(findViewById(R.id.constrain_contacto), "conceder permisos de $texto", Snackbar.LENGTH_INDEFINITE)
                .setAction("Configuración") { openSettings() }
                .addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(snackbar: Snackbar?, event: Int) {
                        super.onDismissed(snackbar, event)
                        if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {

                            comprobarPermisos()
                        }
                    }
                }).show()
    }

    override fun getPackageName(): String {
        return this.javaClass.name.replace("." + this.javaClass.simpleName, "")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK)
            comprobarPermisos()
    }

    private fun openSettings() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivityForResult(intent, CODIGORESULT)
    }

    companion object {
        const val CODIGORESULT = 10
        const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }
}
