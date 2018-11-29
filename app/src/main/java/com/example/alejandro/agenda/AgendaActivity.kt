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
import org.json.JSONArray
import android.widget.Toast
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter




class AgendaActivity : AppCompatActivity(), DataPassListener, DataBaseListener {



    private var manager: FragmentManager? = null
    private var agendaDB: AgendaBaseDatos? = null
    private var ident: IntArray? = null
    private var contactos: ArrayList<Contacto> = ArrayList()
    private var telefono: String? = null
    private var permitCode: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)
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
        val contactoAux = agendaDB!!.buscarContacto(id)
        telefono = contactoAux.telefono
        return contactoAux
    }

    override fun createContact(nombre: String, direccion: String, movil: String, telefono: String, correo: String) {
        agendaDB!!.insertarContacto(nombre, direccion, movil, telefono, correo)
    }

    override fun deleteContact(id: Int) {
        agendaDB!!.borrarContacto(id)
    }

    override fun exportJsonData() {
        permitCode = 1
        comprobarPermisos()
    }

    override fun importJsonData() {
        permitCode = 2
        comprobarPermisos()
    }

    override fun callContact() {
        permitCode = 3
       comprobarPermisos()
    }

    override fun databaseInstance(): AgendaBaseDatos {
        return agendaDB!!
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE ->

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    try {

                        val miArchivo = OutputStreamWriter(openFileOutput(AgendaFragment.ARCHIVO, Activity.MODE_PRIVATE))
                        miArchivo.write(agendaDB!!.getJson().toString())
                        miArchivo.flush()
                        miArchivo.close()
                    } catch (e: IOException) {
                        val t = Toast.makeText(this, "Error de E/S", Toast.LENGTH_LONG)
                        t.show()
                    }
                } else {
                    Toast.makeText(this, "No se han concedido los permisos necesarios", Toast.LENGTH_LONG).show()
                    permitCode = 1
                    comprobarPermisos()
                }

            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        val jsonString = File(filesDir.toString() + "/" + AgendaFragment.ARCHIVO).inputStream().readBytes().toString(Charsets.UTF_8)
                        val objArray = JSONArray(jsonString)
                        for (i in 0..(objArray.length() - 1)) {

                            val obj = objArray.getJSONObject(i)
                            val nombre = obj.getString("nombre")
                            val direccion = obj.getString("direccion")
                            val movil = obj.getString("movil")
                            val telefono = obj.getString("telefono")
                            val correo = obj.getString("correo")
                            createContact(nombre, direccion, movil, telefono, correo)
                        }
                    } catch (e: IOException) {
                        val t = Toast.makeText(this, "Error de E/S", Toast.LENGTH_LONG)
                        t.show()
                    }
                } else {
                    Toast.makeText(this, "No se han concedido los permisos necesarios", Toast.LENGTH_LONG).show()
                    permitCode = 2
                    comprobarPermisos()
                }

            MY_PERMISSIONS_REQUEST_CALL_PHONE ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val phoneNumber = String.format("tel: %s", telefono)
                    val dialIntent = Intent(Intent.ACTION_DIAL)
                    dialIntent.data = Uri.parse(phoneNumber)
                    startActivity(dialIntent)
                }else {
                    Toast.makeText(this, "No se han concedido los permisos necesarios", Toast.LENGTH_LONG).show()
                    permitCode = 3
                    comprobarPermisos()
                }
        }
    }



    internal fun comprobarPermisos() {
        var permiso: String? = null
        //val permisos = arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Toast.makeText(this, "This version is not Android 6 or later " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show()
        } else {
            if(permitCode == 1){
                permiso = Manifest.permission.WRITE_EXTERNAL_STORAGE
                if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permiso)) {
                        showSnackBar("escritura en SD")
                    } else {
                        requestPermissions(arrayOf(permiso), permitCode!!)
                    }
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(permiso), permitCode!!)
                }
            }else if (permitCode == 2){
                permiso = Manifest.permission.READ_EXTERNAL_STORAGE
                if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permiso)) {
                        showSnackBar("lectura en SD")
                    } else {
                        requestPermissions(arrayOf(permiso), permitCode!!)
                    }
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(permiso), permitCode!!)
                }
            }else{
                permiso = Manifest.permission.CALL_PHONE
                if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permiso)) {
                        showSnackBar("llamada")
                    } else {
                        requestPermissions(arrayOf(permiso), permitCode!!)
                    }
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(permiso), permitCode!!)
                }
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

    override fun getPackageName(): String {
        return this.javaClass.name.replace("." + this.javaClass.simpleName, "")
    }

    companion object {
        const val CODIGORESULT = 10
        const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
        const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2
        const val MY_PERMISSIONS_REQUEST_CALL_PHONE = 3
    }
}
