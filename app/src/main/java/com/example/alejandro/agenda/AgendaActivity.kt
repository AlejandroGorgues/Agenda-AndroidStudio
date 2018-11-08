package com.example.alejandro.agenda

import android.Manifest
import android.app.Activity
import android.app.ListActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View


import android.widget.ListView
import android.widget.Toast
import org.json.JSONArray
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener






class AgendaActivity : ListActivity() {
    private var iDAct = 0
    private var addContactoB: FloatingActionButton? = null
    private var exportJsonB: FloatingActionButton? = null
    private var importJsonB: FloatingActionButton? = null
    private var agendaDB: AgendaBaseDatos? = null
    private var agendaAdapter: AgendaAdapter? = null
    private var numFilas: Int = 0
    private var ident: IntArray? = null
    private lateinit var listV: ListView
    private lateinit var constraintContacto: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)
        comprobarPermisos()
        constraintContacto = findViewById(R.id.constrain_contacto)
        addContactoB = findViewById(R.id.addContacto)
        addContactoB!!.setOnClickListener { creaContacto() }
        listV = listView

        exportJsonB = findViewById(R.id.exportJson)
        exportJsonB!!.setOnClickListener {
            try {

                val miArchivo = OutputStreamWriter(openFileOutput(ARCHIVO, Activity.MODE_PRIVATE))
                miArchivo.write(agendaDB!!.getJson().toString())
                miArchivo.flush()
                miArchivo.close()
            } catch (e: IOException) {
                val t = Toast.makeText(this, "Error de E/S", Toast.LENGTH_LONG)
                t.show()
            }
        }

        importJsonB = findViewById(R.id.importJson)
        importJsonB!!.setOnClickListener {
            try {
            comprobarPermisos()
            val jsonString =  File(filesDir.toString() + "/"+ARCHIVO ).inputStream().readBytes().toString(Charsets.UTF_8)
            val objArray = JSONArray(jsonString)
                for(i in 0..(objArray.length()-1)){
                    val obj = objArray.getJSONObject(i)
                    val id = obj.getString("_id")
                    val nombre = obj.getString("nombre")
                    val direccion = obj.getString("direccion")
                    val movil = obj.getString("movil")
                    val telefono = obj.getString("telefono")
                    val correo = obj.getString("correo")
                    agendaDB!!.insertarContacto(nombre,direccion,movil,telefono,correo)
                }
                rellenaLista()

            } catch (e: IOException) {
                val t = Toast.makeText(this, "Error de E/S", Toast.LENGTH_LONG)
                t.show()
            }
        }

        listV.onItemLongClickListener = OnItemLongClickListener { parent, view, position, id ->
            iDAct = ident!![position]
            modificarContacto()
            true
        }

        agendaDB = AgendaBaseDatos(this)
        rellenaLista()
        //registerForContextMenu(listView)

    }

    override fun onListItemClick(lv: ListView, view: View, posicion: Int, id: Long) {
        intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "658554492"))
        startActivity(intent)
    }

    fun rellenaLista() {

        numFilas = agendaDB!!.numerodeFilas()
        if (numFilas > 0) {
            ident = agendaDB!!.recuperaIds()
            agendaAdapter = AgendaAdapter(this, agendaDB!!.buscarContactoCursor())
            listAdapter = agendaAdapter
        }else{
            ident = null
            agendaAdapter = AgendaAdapter(this, agendaDB!!.buscarContactoCursor())
            listAdapter = agendaAdapter
        }

    }


    fun creaContacto() {
        val i = Intent(this, CrearContacto::class.java)
        startActivityForResult(i, CODIGOA)
    }

    fun modificarContacto() {
        val miContacto: Contacto = agendaDB!!.buscarContacto(iDAct)
        val i = Intent(this, MostrarContacto::class.java)
        i.putExtra("ID", iDAct)
        i.putExtra("Nombre", miContacto.nombre)
        i.putExtra("Direccion", miContacto.direccion)
        i.putExtra("Movil", miContacto.movil)
        i.putExtra("Telefono", miContacto.telefono)
        i.putExtra("Correo", miContacto.correo)

        startActivityForResult(i, CODIGOM)
    }

    override fun onActivityResult(resul: Int, codigo: Int, data: Intent) {
        if (codigo == Activity.RESULT_OK) {
            if (resul == CODIGOA) {
                val nombre = data.extras!!.getString("Nombre")
                val direccion = data.extras!!.getString("Direccion")
                val movil = data.extras!!.getString("Movil")
                val telefono = data.extras!!.getString("Telefono")
                val correo = data.extras!!.getString("Correo")

                agendaDB!!.insertarContacto(nombre, direccion, movil, telefono, correo)
                rellenaLista()

            } else {
                val nombre = data.extras!!.getString("Nombre")
                val direccion = data.extras!!.getString("Direccion")
                val movil = data.extras!!.getString("Movil")
                val telefono = data.extras!!.getString("Telefono")
                val correo = data.extras!!.getString("Correo")
                val mid = data.extras!!.getInt("ID")
                agendaDB!!.modificarContacto(mid, nombre, direccion, movil, telefono, correo)
                rellenaLista()
            }

        } else if (codigo == RESULT_BORRAR) {
            val mid = data.extras!!.getInt("ID")
            agendaDB!!.borrarContacto(mid)
            rellenaLista()
        }
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
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                permisos[1]) ) {

                    showSnackBar("escritura en SD")

                }else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                permisos[2])){
                    showSnackBar("callPhone")
                } else {
                    requestPermissions(
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

    private fun openSettings() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivityForResult(intent, CODIGOA)
    }

    companion object {
        val CODIGOA = 12
        val CODIGOM = 13
        val RESULT_BORRAR = 14
        val ARCHIVO = "contactos.CNT"
        const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }
}
