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

        transaction.add(R.id.agendaActivityLayout, fragmentLista)
        transaction.commit()


    }



    //A partir del valor asociado al fragment, se remplaca el actual con los valores que hay en data
    override fun passData(data: Bundle, fragment: Int) {
        val transaction = manager!!.beginTransaction()
        val fragmentReplace: Fragment
            when (fragment) {
                0 -> {
                    fragmentReplace = AgendaFragment()
                    fragmentReplace.arguments = data
                    transaction.replace(R.id.agendaActivityLayout, fragmentReplace)

                }
                1 -> {

                    fragmentReplace = CrearContactoFragment()
                    fragmentReplace.arguments = data
                    transaction.replace(R.id.agendaActivityLayout, fragmentReplace)

                }
                else -> {


                    fragmentReplace = MostrarContactoFragment()
                    fragmentReplace.arguments = data
                    transaction.replace(R.id.agendaActivityLayout, fragmentReplace)


                }
            }
        transaction.commit()
    }

    //Se modifica el usuario en la base de datos asociado a la id
    override fun modifiedDataContact(id: Int, nombre: String, direccion: String, movil: String, telefono: String, correo: String) {
        agendaDB!!.modificarContacto(id, nombre, direccion, movil, telefono, correo)
    }

    //Devuelve el array entero de identificadores y los contactos que corresponden a cada identificador
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

    //Se devuelve el contacto que se busca en la base de datos y se guarda el número de teléfono
    //asociado al usuario en caso de que realice una llamada
    override fun searchDataContact(id: Int): Contacto {
        val contactoAux = agendaDB!!.buscarContacto(id)
        telefono = contactoAux.telefono
        return contactoAux
    }

    //Inserta un contacto en la base de datos
    override fun createContact(nombre: String, direccion: String, movil: String, telefono: String, correo: String) {
        agendaDB!!.insertarContacto(nombre, direccion, movil, telefono, correo)
    }

    //Elimina un contacto de la base de datos
    override fun deleteContact(id: Int) {
        agendaDB!!.borrarContacto(id)
    }

    //Comprueba si la tarjeta SD está en el dispositivo y que los permisos están dados para exportar
    //la base de datos a un archivo .CNT
    override fun exportJsonData() {
        val estado = Environment.getExternalStorageState()
        if (estado == Environment.MEDIA_MOUNTED) {
            permitCode = 1
            permiso = Manifest.permission.WRITE_EXTERNAL_STORAGE
            comprobarPermisos()
        } else {
            Toast.makeText(this, resources.getString(R.string.errorAccesoSD), Toast.LENGTH_SHORT).show()
        }

    }

    //Comprueba si la tarjeta SD está en el dispositivo y que los permisos están dados para importar
    //los contactos de un archivo .CNT a la base de datos
    override fun importJsonData() {
        val estado = Environment.getExternalStorageState()
        if (estado == Environment.MEDIA_MOUNTED || estado == Environment.MEDIA_MOUNTED_READ_ONLY) {
            permitCode = 2
            permiso = Manifest.permission.READ_EXTERNAL_STORAGE
            comprobarPermisos()
        } else {
            Toast.makeText(this, resources.getString(R.string.errorAccesoSD), Toast.LENGTH_SHORT).show()
        }

    }

    //Llama al contacto a partir del número de telefono
    override fun callContact() {
        val phoneNumber = String.format("tel: %s", telefono)
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse(phoneNumber)
        startActivity(dialIntent)
    }

    //Obtiene la instancia de la base de datos
    override fun databaseInstance(): AgendaBaseDatos {
        return agendaDB!!
    }

    //Comprueba si los permisos estan dados
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            //Si es un permiso de escritura
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE ->

                //y el permiso esta dado
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    //procede a ejecutar el código correpsondiente
                    tipo = 1
                    metodosPermisos()
                } else {
                    Toast.makeText(this,resources.getString(R.string.errorEscritura), Toast.LENGTH_LONG).show()
                }

            //Si es un permiso de lectura
            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE ->

                //y el permiso esta dado
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //procede a ejecutar el código correpsondiente
                    tipo = 2
                    metodosPermisos()
                } else {
                    Toast.makeText(this, resources.getString(R.string.errorLectura), Toast.LENGTH_LONG).show()
                }
        }
    }



    //Comprueba si los permisos estan dados y si no, los solicita
    private fun comprobarPermisos() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Toast.makeText(this, resources.getString(R.string.oldVersion), Toast.LENGTH_LONG).show()
            metodosPermisos()
        } else {

            //Si no estan dados, los solicita
            if (ContextCompat.checkSelfPermission(this, permiso!!) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permiso!!)) {

                    ActivityCompat.requestPermissions(this, arrayOf(permiso), permitCode!!)
                } else {

                    ActivityCompat.requestPermissions(this, arrayOf(permiso), permitCode!!)
                }
                //Si estan dados, procede a ejectuar el código correspondiente
            } else {
                tipo = if(permiso == Manifest.permission.WRITE_EXTERNAL_STORAGE){
                    1
                }else{
                    2
                }
                metodosPermisos()
            }
        }
    }

    private fun metodosPermisos(){
        when (tipo) {
            1 -> {
                //Escribe en un archivo .CNT los datos de la base de datos
                try {
                    val ruta = Environment.getExternalStorageDirectory()
                    val f = File(ruta.absolutePath, ARCHIVO)
                    val miArchivo =  OutputStreamWriter( FileOutputStream(f))
                    miArchivo.write(agendaDB!!.getJson().toString())
                    miArchivo.flush()
                    miArchivo.close()
                } catch (e: IOException) {
                    val t = Toast.makeText(this, resources.getString(R.string.errorExportar), Toast.LENGTH_LONG)
                    t.show()
                }
            }
            2 -> {
                //Lee de un archivo .CNT los datos y los almacena en la base de datos
                try {

                    val yourFile = File(Environment.getExternalStorageDirectory().absolutePath, ARCHIVO)
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
                    val t = Toast.makeText(this, resources.getString(R.string.errorImportar), Toast.LENGTH_LONG)
                    t.show()
                }
            }
        }
    }

    companion object {
        const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
        const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2
        const val ARCHIVO = "contactos.CNT"
    }
}
