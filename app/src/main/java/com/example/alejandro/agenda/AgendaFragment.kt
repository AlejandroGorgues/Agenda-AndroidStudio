package com.example.alejandro.agenda


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import es.upm.etsisi.mirecyclerview.SwipeContactoTouch
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import org.json.JSONArray
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter


class AgendaFragment : Fragment() {

    private var iDAct = 0
    private var agendaDB: AgendaBaseDatos? = null
    private var agendaAdapter: AgendaAdapter? = null
    private var numFilas: Int = 0
    private var ident: IntArray? = null
    private lateinit var recyclerAgenda: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var constraintContacto: View
    private var contactos: ArrayList<Contacto> = ArrayList()

    private var dataPass: DataPassListener? = null

    private lateinit var addContactoFloatingB: FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_agenda, container, false)

        toolbar = view.findViewById(R.id.agendaToolbar)
        (activity as AgendaActivity).setSupportActionBar(toolbar)

        recyclerAgenda = view.findViewById(R.id.listAgenda)
        agendaDB = AgendaBaseDatos(activity as AgendaActivity)


        rellenaLista()
        agendaAdapter = AgendaAdapter(contactos, agendaDB!!)
        agendaAdapter!!.setOnClickListener(View.OnClickListener { v ->
            iDAct = recyclerAgenda.getChildLayoutPosition(v)
            val popupMenu = PopupMenu(activity, v)
            popupMenu.inflate(R.menu.menu_gestion_contacto)
            popupMenu.setOnMenuItemClickListener { item ->
                when {
                    item.itemId == R.id.llamarC -> {
                        val phoneNumber = String.format("tel: %s",agendaDB!!.buscarContacto(iDAct).telefono)
                        val dialIntent = Intent(Intent.ACTION_DIAL)
                        dialIntent.data = Uri.parse(phoneNumber)
                        startActivity(dialIntent)
                        true
                    }
                    item.itemId == R.id.modificarC -> {
                        modificarContacto()
                        true
                    }
                    else -> {
                        numFilas -= 1
                        agendaDB!!.borrarContacto(iDAct)
                        rellenaLista()
                        agendaAdapter!!.notifyDataSetChanged()
                        true
                    }
                }

            }
            popupMenu.show()

        })
        inicializarReciclerView()

        val callback = SwipeContactoTouch(agendaAdapter as ContactoTouchAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerAgenda)

        addContactoFloatingB = view.findViewById(R.id.floatCrearCliente)
        addContactoFloatingB.setOnClickListener {
            numFilas += 1
            creaContacto() }

        comprobarPermisos()
        constraintContacto = view.findViewById(R.id.constrain_contacto)

        return view
    }

    interface DataPassListener {
        fun passData(data: Bundle, fragment:Int)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            dataPass = context as DataPassListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement OnImageClickListener")
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add-> {
                creaContacto()
                return true
            }
            R.id.action_exportarJSON -> {
                try {

                    val miArchivo = OutputStreamWriter(activity!!.openFileOutput(ARCHIVO, Activity.MODE_PRIVATE))
                    miArchivo.write(agendaDB!!.getJson().toString())
                    miArchivo.flush()
                    miArchivo.close()
                } catch (e: IOException) {
                    val t = Toast.makeText(activity, "Error de E/S", Toast.LENGTH_LONG)
                    t.show()
                }

                return true
            }
            R.id.action_importarJSON -> {
                try {
                    comprobarPermisos()
                    val jsonString =  File(activity!!.filesDir.toString() + "/"+ARCHIVO ).inputStream().readBytes().toString(Charsets.UTF_8)
                    val objArray = JSONArray(jsonString)
                    for(i in 0..(objArray.length()-1)){
                        numFilas += 1
                        val obj = objArray.getJSONObject(i)
                        val nombre = obj.getString("nombre")
                        val direccion = obj.getString("direccion")
                        val movil = obj.getString("movil")
                        val telefono = obj.getString("telefono")
                        val correo = obj.getString("correo")
                        agendaDB!!.insertarContacto(numFilas,nombre,direccion,movil,telefono,correo)
                    }
                    rellenaLista()
                    agendaAdapter!!.notifyDataSetChanged()

                } catch (e: IOException) {
                    val t = Toast.makeText(activity, "Error de E/S", Toast.LENGTH_LONG)
                    t.show()
                }


                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun inicializarReciclerView(){
        recyclerAgenda.adapter = agendaAdapter
        recyclerAgenda.layoutManager = LinearLayoutManager(activity)
        recyclerAgenda.itemAnimator = DefaultItemAnimator()
    }

    private fun rellenaLista() {
        numFilas = agendaDB!!.numerodeFilas()
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

    }

    fun creaContacto() {
        dataPass!!.passData(Bundle(), 1)
    }

    fun modificarContacto() {
        val miContacto: Contacto = agendaDB!!.buscarContacto(iDAct)
        val bundle = Bundle()
        bundle.putInt("ID", iDAct)
        bundle.putString("Nombre", miContacto.nombre)
        bundle.putString("Direccion", miContacto.direccion)
        bundle.putString("Movil", miContacto.movil)
        bundle.putString("Telefono", miContacto.telefono)
        bundle.putString("Correo", miContacto.correo)
        bundle.putInt("idContacto", iDAct)

        dataPass!!.passData(bundle, 2)
    }

    fun onReturnFromBackStack(resul: Int, codigo: Int, data: Bundle){
        if(!data.isEmpty) {
            if (codigo == Activity.RESULT_OK) {
                if (resul == CODIGOA) {
                    numFilas += 1
                    val nombre = data.getString("Nombre")
                    val direccion = data.getString("Direccion")
                    val movil = data.getString("Movil")
                    val telefono = data.getString("Telefono")
                    val correo = data.getString("Correo")
                    agendaDB!!.insertarContacto(numFilas, nombre, direccion, movil, telefono, correo)
                    rellenaLista()
                    agendaAdapter!!.notifyDataSetChanged()

                } else if (resul == CODIGOM) {
                    val nombre = data.getString("Nombre")
                    val direccion = data.getString("Direccion")
                    val movil = data.getString("Movil")
                    val telefono = data.getString("Telefono")
                    val correo = data.getString("Correo")
                    val mid = data.getInt("ID")
                    iDAct = data.getInt("idContacto")
                    agendaDB!!.modificarContacto(mid, nombre, direccion, movil, telefono, correo)

                    contactos[iDAct] = Contacto(mid, nombre, direccion, movil, telefono, correo)
                    agendaAdapter!!.notifyItemChanged(iDAct)

                }
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        val t1: Toast


        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE ->

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    t1 = Toast.makeText(activity, "permisos de escritura concedidios", Toast.LENGTH_LONG)
                    t1.show()
                } else {
                    val t = Toast.makeText(activity, "No se han concedido los permisos necesarios", Toast.LENGTH_LONG)
                    t.show()
                    comprobarPermisos()
                }
        }
    }

    internal fun comprobarPermisos() {
        val permisos = arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Toast.makeText(activity, "This version is not Android 6 or later " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show()
        } else {
            if ((ContextCompat.checkSelfPermission(activity!!,
                            permisos[1]) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(activity!!,
                            permisos[2]) != PackageManager.PERMISSION_GRANTED) ){
                when {
                    ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                            permisos[1]) -> showSnackBar("escritura en SD")
                    ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                            permisos[2]) -> showSnackBar("callPhone")
                    else -> requestPermissions(
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE), MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
                }
            } else {
                ActivityCompat.requestPermissions(activity!!,
                        arrayOf(permisos[1], permisos[2]), MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun showSnackBar(texto: String) {
        Snackbar.make(view!!.findViewById(R.id.constrain_contacto), "conceder permisos de $texto", Snackbar.LENGTH_INDEFINITE)
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

    private fun getPackageName(): String {
        return this.javaClass.name.replace("." + this.javaClass.simpleName, "")
    }

    private fun openSettings() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:getPackageName()")
        startActivityForResult(intent, CODIGOA)
    }

    companion object {
        val CODIGOA = 12
        val CODIGOM = 13
        val ARCHIVO = "contactos.CNT"
        const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }

}
