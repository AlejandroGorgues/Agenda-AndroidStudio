package com.example.alejandro.agenda

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.support.v7.widget.helper.ItemTouchHelper
import android.widget.PopupMenu
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*


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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_agenda, container, false)
        activityDataBaseListener = activity as DataBaseListener
        activityPassData = activity as DataPassListener


        setHasOptionsMenu(true)
        val toolbar = view.findViewById<Toolbar>(R.id.agendaToolbar)
        (activity as AgendaActivity).setSupportActionBar(toolbar)

        recyclerAgenda = view.findViewById(R.id.listAgenda)



        rellenaLista()
        agendaAdapter = AgendaAdapter(contactos, activityDataBaseListener.databaseInstance())
        agendaAdapter!!.setOnClickListener(View.OnClickListener { v ->
            iDAct = ident!![recyclerAgenda.getChildLayoutPosition(v)]
            val popupMenu = PopupMenu(activity, v)
            popupMenu.inflate(R.menu.menu_gestion_contacto)
            popupMenu.setOnMenuItemClickListener { item ->
                when {
                    item.itemId == R.id.llamarC -> {
                        activityDataBaseListener.searchDataContact(iDAct)
                        activityDataBaseListener.callContact()
                        true
                    }
                    item.itemId == R.id.modificarC -> {
                        modificarContacto()
                        true
                    }
                    else -> {
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

        val callback = SwipeContactoTouch(agendaAdapter as ContactoTouchAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerAgenda)

        addContactoFloatingB = view.findViewById(R.id.floatCrearCliente)
        addContactoFloatingB.setOnClickListener {
            creaContacto() }
        constraintContacto = view.findViewById(R.id.constrain_contacto)

        return view
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        activity!!.menuInflater.inflate(R.menu.menu_agenda, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add-> {
                creaContacto()
                return true
            }
            R.id.action_exportarJSON -> {
                activityDataBaseListener.exportJsonData()

                return true
            }
            R.id.action_importarJSON -> {

                activityDataBaseListener.importJsonData()
                rellenaLista()
                agendaAdapter!!.notifyDataSetChanged()

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
        val pair = activityDataBaseListener.returnIdentContact()
        ident = pair.first
        contactos = pair.second

    }

    fun creaContacto() {

        activityPassData.passData(Bundle(), 1)
    }

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

    companion object {
        const val ARCHIVO = "contactos.CNT"
    }

}
