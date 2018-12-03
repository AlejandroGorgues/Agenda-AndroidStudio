package com.example.alejandro.agenda

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build

import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import org.json.JSONArray
import android.widget.Toast
import com.example.alejandro.agenda.fragments.AgendaFragment
import com.example.alejandro.agenda.fragments.CrearContactoFragment
import com.example.alejandro.agenda.fragments.MostrarContactoFragment
import com.example.alejandro.agenda.interfaces.DataBaseListener
import com.example.alejandro.agenda.interfaces.DataPassListener
import com.example.alejandro.agenda.model.Contacto
import java.io.*
import java.nio.channels.FileChannel
import java.nio.charset.Charset




class AgendaActivity : AppCompatActivity(), DataPassListener, DataBaseListener {

    private var manager: FragmentManager? = null
    private var agendaDB: AgendaBaseDatos? = null
    private var ident: IntArray? = null
    private var contactos: ArrayList<Contacto> = ArrayList()
    private var telefono: String? = null
    private var permitCode: Int? = null
    private var permiso: String? = null
    private var tipo: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)
        agendaDB = AgendaBaseDatos(this)
        manager = supportFragmentManager

        val transaction = manager!!.beginTransaction()
        val fragmentLista = AgendaFragment()

        transaction.add(R.id.agendaActivityLayout, fragmentLista, fragmentPrincipal)
        transaction.addToBackStack(null)
        transaction.commit()


    }


    override fun passData(data: Bundle, fragment: Int) {
        val transaction = manager!!.beginTransaction()
        val fragmentReplace: Fragment
            when (fragment) {
                0 -> {
                    fragmentReplace = if( manager!!.findFragmentByTag(fragmentPrincipal) != null) {
                        manager!!.findFragmentByTag(fragmentPrincipal) as AgendaFragment
                    }else{
                        AgendaFragment()
                    }

                    fragmentReplace.arguments = data
                    transaction.replace(R.id.agendaActivityLayout, fragmentReplace, fragmentPrincipal)
                    transaction.addToBackStack(null)

                }
                1 -> {

                    fragmentReplace = if( manager!!.findFragmentByTag(fragmentCrear) != null) {
                        manager!!.findFragmentByTag(fragmentCrear) as CrearContactoFragment
                    }else{
                        CrearContactoFragment()
                    }

                    fragmentReplace.arguments = data
                    transaction.replace(R.id.agendaActivityLayout, fragmentReplace, fragmentCrear)
                    transaction.addToBackStack(null)

                }
                else -> {


                    fragmentReplace = if( manager!!.findFragmentByTag(fragmentMostrar) != null) {
                        manager!!.findFragmentByTag(fragmentMostrar) as MostrarContactoFragment
                    }else{
                        MostrarContactoFragment()
                    }

                    fragmentReplace.arguments = data

                    transaction.replace(R.id.agendaActivityLayout, fragmentReplace, fragmentMostrar)
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
        val estado = Environment.getExternalStorageState()
        if (estado == Environment.MEDIA_MOUNTED) {
            Log.e("aqui", "aqui")
            permitCode = 1
            permiso = Manifest.permission.WRITE_EXTERNAL_STORAGE
            comprobarPermisos()
        } else {
            Toast.makeText(this, "Fallo al acceso de la tarjeta SD", Toast.LENGTH_SHORT).show();
        }

    }

    override fun importJsonData() {
        val estado = Environment.getExternalStorageState()
        if (estado == Environment.MEDIA_MOUNTED || estado == Environment.MEDIA_MOUNTED_READ_ONLY) {
            permitCode = 2
            permiso = Manifest.permission.READ_EXTERNAL_STORAGE
            comprobarPermisos()
        } else {
            Toast.makeText(this, "Fallo al acceso de la tarjeta SD", Toast.LENGTH_SHORT).show();
        }

    }

    override fun callContact() {
        permitCode = 3
        permiso = Manifest.permission.CALL_PHONE
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


                    tipo = 1
                    metodosPermisos()
                } else {
                    Toast.makeText(this, R.string.errorEscritura, Toast.LENGTH_LONG).show()
                }

            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tipo = 2
                    metodosPermisos()
                } else {
                    Toast.makeText(this, R.string.errorLectura, Toast.LENGTH_LONG).show()
                }

            MY_PERMISSIONS_REQUEST_CALL_PHONE ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tipo = 3
                   metodosPermisos()
                }else {
                    Toast.makeText(this, R.string.errorLlamada, Toast.LENGTH_LONG).show()
                }
        }
    }



    private fun comprobarPermisos() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Toast.makeText(this, "This version is not Android 6 or later " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show()
            metodosPermisos()
        } else {

            if (ContextCompat.checkSelfPermission(this, permiso!!) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permiso!!)) {

                    ActivityCompat.requestPermissions(this, arrayOf(permiso), permitCode!!)
                } else {

                    ActivityCompat.requestPermissions(this, arrayOf(permiso), permitCode!!)
                }
            } else {
                tipo = if(permiso == Manifest.permission.WRITE_EXTERNAL_STORAGE){
                    1
                }else if (permiso == Manifest.permission.READ_EXTERNAL_STORAGE){
                    2
                }else{
                    3
                }
                metodosPermisos()
            }
        }
    }

    private fun metodosPermisos(){
        when (tipo) {
            1 -> {
                try {
                    val ruta = Environment.getExternalStorageDirectory()
                    val f = File(ruta.absolutePath, "contactos.CNT")
                    //val miArchivo = OutputStreamWriter(openFileOutput(ARCHIVO, Activity.MODE_PRIVATE))
                    val miArchivo =  OutputStreamWriter( FileOutputStream(f))
                    miArchivo.write(agendaDB!!.getJson().toString())
                    miArchivo.flush()
                    miArchivo.close()
                } catch (e: IOException) {
                    val t = Toast.makeText(this, R.string.errorExportar, Toast.LENGTH_LONG)
                    t.show()
                }
            }
            2 -> {
                try {
                    //val f = File(ruta.absolutePath, "contactos.CNT")
                    //val fRead = BufferedReader(InputStreamReader(FileInputStream(f)))
                    val yourFile = File(Environment.getExternalStorageDirectory().absolutePath, "contactos.CNT")
                    val stream = FileInputStream(yourFile)
                    var jsonStr: String? = null
                    try {
                        val fc = stream.channel
                        val bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size())

                        jsonStr = Charset.defaultCharset().decode(bb).toString()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        stream.close()
                    }

                    val objArray = JSONArray(jsonStr)

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
                    val t = Toast.makeText(this, R.string.errorImportar, Toast.LENGTH_LONG)
                    t.show()
                }
            }
            else -> {
                val phoneNumber = String.format("tel: %s", telefono)
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse(phoneNumber)
                startActivity(dialIntent)
            }
        }
    }

    companion object {
        const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
        const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2
        const val MY_PERMISSIONS_REQUEST_CALL_PHONE = 3
        const val ARCHIVO = "contactos.CNT"
        const val fragmentPrincipal = "fragmentPrincipal"
        const val fragmentCrear = "fragmentCrear"
        const val fragmentMostrar = "fragmentMostrar"
    }
}
