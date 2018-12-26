package com.example.alejandro.agenda.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.*
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Editable
import android.text.TextWatcher
import android.widget.PopupMenu
import android.util.Log
import android.view.*
import android.widget.EditText
import com.example.alejandro.agenda.*
import com.example.alejandro.agenda.interfaces.ContactoTouchAdapter
import com.example.alejandro.agenda.interfaces.DataBaseListener
import com.example.alejandro.agenda.interfaces.DataPassListener
import com.example.alejandro.agenda.interfaces.ReloadDataAdapter
import com.example.alejandro.agenda.model.Contacto


class AgendaFragment : Fragment()  {


    private var iDAct = 0

    private var agendaAdapter: AgendaAdapter? = null

    private lateinit var recyclerAgenda: RecyclerView
    private lateinit var constraintContacto: View
    private var ident: IntArray? = null
    private var contactos: ArrayList<Contacto> = ArrayList()

    private lateinit var activityDataBaseListener: DataBaseListener
    private lateinit var activityPassData: DataPassListener

    private lateinit var addContactoFloatingB: FloatingActionButton

    private lateinit var searchC: EditText

    private lateinit var tilSearch: TextInputLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_agenda, container, false)
        activityDataBaseListener = activity as DataBaseListener
        activityPassData = activity as DataPassListener

        recyclerAgenda = view.findViewById(R.id.listAgenda)

        val reloadInterface = object: ReloadDataAdapter {
            override fun reloadListAdapter() {
                val pair = activityDataBaseListener.returnIdentContact()
                ident = pair.first
                contactos = pair.second
            }

        }

        //Rellena el adaptador a partir del array de contactos y asocia un evento onClickListener a
        //cada elemento de la lista
        rellenaLista()
        agendaAdapter = AgendaAdapter(contactos, activityDataBaseListener.databaseInstance(), context!!, reloadInterface)
        agendaAdapter!!.setOnClickListener(View.OnClickListener { v ->

            iDAct = if(agendaAdapter!!.getContactosAux().second){
                val contactoPrueba = agendaAdapter!!.getContactosAux().first[recyclerAgenda.getChildLayoutPosition(v)]
                contactoPrueba.id
            }else{
                ident!![recyclerAgenda.getChildLayoutPosition(v)]
            }

            val popupMenu = PopupMenu(activity, v)
            popupMenu.inflate(R.menu.menu_gestion_contacto)
            popupMenu.setOnMenuItemClickListener { item ->
                when {
                    //Si decide llamar busca al contacto y llama al número de teléfono obtenido
                    item.itemId == R.id.llamarC -> {
                        activityDataBaseListener.searchDataContact(iDAct)
                        activityDataBaseListener.callContact()
                        true
                    }
                    //Si decide modificar el elemento, cambia de fragment
                    item.itemId == R.id.modificarC -> {
                        modificarContacto()
                        true
                    }
                    else -> {
                        //Si decide eliminar el elemento, lo eleimina y actualiza la lista
                        activityDataBaseListener.deleteContact(iDAct)
                        rellenaLista()
                        agendaAdapter!!.notifyDataSetChanged()
                        true
                    }
                }
            }
            popupMenu.show()

        })
        inicializarReciclerView()

        //Asocia los eventos táctiles
        val callback = SwipeContactoTouch(agendaAdapter as ContactoTouchAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerAgenda)

        addContactoFloatingB = view.findViewById(R.id.floatCrearCliente)
        addContactoFloatingB.setOnClickListener {
            creaContacto() }
        constraintContacto = view.findViewById(R.id.constrain_contacto)

        searchC = view.findViewById(R.id.searchContacto)

        tilSearch = view.findViewById(R.id.til_search)


        searchC.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        agendaAdapter!!.filter.filter(s)
                    }

                    override fun afterTextChanged(p0: Editable?) {

                    }


                })

        return view
    }

    private fun inicializarReciclerView(){
        recyclerAgenda.adapter = agendaAdapter
        recyclerAgenda.layoutManager = LinearLayoutManager(activity)
        recyclerAgenda.itemAnimator = DefaultItemAnimator()
    }

    //Obtiene los valores de los arrays y los guarda en los atributos correspondientes
    private fun rellenaLista() {
        val pair = activityDataBaseListener.returnIdentContact()
        ident = pair.first
        contactos = pair.second
    }



    fun notificarListaLlena(){
        rellenaLista()
        agendaAdapter!!.notifyDataSetChanged()
    }

    fun creaContacto() {

        activityPassData.passData(Bundle(), 1)
    }

    //Carga los valores del contacto a modificar en un bundle que luego van a ser extraidos en
    //el fragment correspondiente
    fun modificarContacto() {
        val miContacto: Contacto = activityDataBaseListener.searchDataContact(iDAct)
        val bundle = Bundle()
        bundle.putInt("ID", iDAct)
        bundle.putString("Nombre", miContacto.nombre)
        bundle.putString("Direccion", miContacto.direccion)
        bundle.putString("Movil", miContacto.movil)
        bundle.putString("Telefono", miContacto.telefono)
        bundle.putString("Correo", miContacto.correo)

        activityPassData.passData(bundle, 2)
    }

}
