package com.example.alejandro.agenda

import android.app.Activity
import android.app.ListActivity
import android.content.Intent

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button

import android.widget.ListView
import android.widget.TextView


class AgendaActivity : ListActivity() {
    private var iDAct = 0
    private var addContactoB: FloatingActionButton? = null //, bBorrar,bModificar;
    private var agendaDB: AgendaBaseDatos? = null
    private var agendaAdapter: AgendaAdapter? = null
    // private ListAdapter adaptador;
    private var numFilas: Int = 0
    private var ident: IntArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)
        addContactoB = findViewById(R.id.addContacto)
        addContactoB!!.setOnClickListener { view -> creaContacto(view) }
        // Lo primero será crear un objeto de la clase MiBaseDatos al que pasaremos el contexto de la aplicación
        agendaDB = AgendaBaseDatos(this)
        rellenaLista()
        //registerForContextMenu(listView)

    }

    //TODO: ESTA MAL, NO LO ENTIENDO HAY QUE REVISARLO
    override fun onListItemClick(lv: ListView, view: View, posicion: Int, id: Long) {
        //val miId = findViewById<TextView>(R.id.cNombre)
        iDAct = ident!![posicion]
        modificarContacto()

    }


    fun rellenaLista() {

        numFilas = agendaDB!!.numerodeFilas()
        if (numFilas > 0) {
            ident = agendaDB!!.recuperaIds()
            agendaAdapter = AgendaAdapter(this, agendaDB!!.buscarContactoCursor())


            /*  se puede hacer mas facil con un objeto de tipo SimpleCursorAdapter
          adaptador = new SimpleCursorAdapter(this,
                                               R.layout.elementolista,
                                              MDB.recuperarNotasCursor(),
                                             new String[] { "_id", "fecha"},
                                             new int[] { R.id.tVID, R.id.tVFecha},

                                             FLAG_REGISTER_CONTENT_OBSERVER);*/
            listAdapter = agendaAdapter
        }

    }


    fun creaContacto(vista: View) {
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
    /*fun eliminaNota(vista: View)
        {
            val i = Intent(this, MuestraNota::class.java)
            i.putExtra("ID", iDAct)


        }*/


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
                // adaptador.notifyDataSetChanged(); // metodo para notificar que los datos han cambiado
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


    /*fun inserta() {
        agendaDB!!.insertarNota("Esta es la primera nota", "22/09/1965")
        agendaDB!!.insertarNota("Esta es la segunda nota", "22/09/1985")
    }*/

   /* override// Método donde definimos el menú contextual cuando se despliega
    fun onCreateContextMenu(menu: ContextMenu, v: View,
                            menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        //Inflador del menú contextual
        val inflater = menuInflater
        // Si el componente que vamos a dibujar es la etiqueta usamos
        // el fichero XML correspondiente
        if (v.id == listView.id)

            menu.setHeaderTitle("Menu Contextual")
        inflater.inflate(R.menu.menu_context_lista, menu)
        // Si el componente que vamos a dibujar es el ListView usamos
        // el fichero XML correspondiente

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val menuItemIndex = item.itemId
        when (item.itemId) {
            // Se selecciona la opción 1 de menú contextual de la etiqueta
            R.id.Editar -> {
                iDAct = ident!![info.position]
                modificarNota(listView)
                return true
            }
            // Se selecciona la opción 2 de menú contextual de la etiqueta
            R.id.Eliminar -> {
                iDAct = ident!![info.position]
                MDB!!.borrarNota(iDAct)
                rellenaLista()
                return true
            }

            R.id.Nuevo -> {
                iDAct = ident!![info.position]
                creaNota(listView)
                rellenaLista()
                return true
            }


            else -> return super.onContextItemSelected(item)
        }


    }*/

    companion object {
        val CODIGOA = 12
        val CODIGOM = 13
        val RESULT_BORRAR = 14
    }


}
