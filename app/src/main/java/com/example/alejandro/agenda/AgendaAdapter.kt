package com.example.alejandro.agenda

import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import android.view.LayoutInflater
import com.example.alejandro.agenda.interfaces.ContactoTouchAdapter
import com.example.alejandro.agenda.model.Contacto
import java.util.*


class AgendaAdapter(private val contactos : ArrayList<Contacto>, private val agendaDB: AgendaBaseDatos) : RecyclerView.Adapter<AgendaAdapter.ContactosViewHolder>(), ContactoTouchAdapter, View.OnClickListener {


    private var listener: View.OnClickListener? = null



    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): ContactosViewHolder {
        val vh = LayoutInflater.from(parent.context).inflate(R.layout.contacto_lista, parent, false)
        vh.setOnClickListener(this)
        return ContactosViewHolder(vh)
    }

    override fun getItemCount(): Int {
        return contactos.size
    }

    override fun onBindViewHolder(cvh: ContactosViewHolder, pos: Int) {
        val item = contactos[pos]
        cvh.bindContactos(item)
    }

    override fun onClick(view: View?) {
        if (listener != null)
            listener!!.onClick(view)
    }

    fun setOnClickListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    override fun onMoverItem(fromPosition: Int, toPosition: Int) {
        val contactoAux = contactos[fromPosition]

        agendaDB.modificarContacto(contactos[fromPosition].id, contactos[toPosition].nombre!!, contactos[toPosition].direccion!!, contactos[toPosition].movil!!, contactos[toPosition].telefono!!, contactos[toPosition].correo!!)
        agendaDB.modificarContacto(contactos[toPosition].id, contactoAux.nombre!!, contactoAux.direccion!!, contactoAux.movil!!, contactoAux.telefono!!, contactoAux.correo!!)
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(contactos, i, i + 1) //método java para intercambiar las posiciones de elementos en un array
                val idAux = contactos[i].id
                contactos[i].id = contactos[i+1].id
                contactos[i+1].id = idAux
            }
        } else {
            for (i in fromPosition downTo toPosition +1) {
                Collections.swap(contactos, i, i - 1)
                val idAux = contactos[i].id
                contactos[i].id = contactos[i-1].id
                contactos[i-1].id = idAux
            }
        }

        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onEliminarItem(position: Int) {
        agendaDB.borrarContacto(contactos[position].id)
        contactos.removeAt(position)
        notifyItemRemoved(position)

    }



    class ContactosViewHolder (viewC: View) : RecyclerView.ViewHolder(viewC) {

        private var nombreC: TextView = viewC.findViewById(R.id.cNombre)
        private var telefonoC: TextView = viewC.findViewById(R.id.cTelefono)

        fun bindContactos(c: Contacto) {
            nombreC.text = c.nombre
            telefonoC.text = c.telefono

        }

    }



}

