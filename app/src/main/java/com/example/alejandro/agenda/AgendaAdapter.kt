package com.example.alejandro.agenda

import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import java.util.ArrayList




class AgendaAdapter(private val contactos : ArrayList<Contacto>) : RecyclerView.Adapter<AgendaAdapter.ContactosViewHolder>(), View.OnLongClickListener{


    private var listener: View.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ContactosViewHolder {
        val vh = LayoutInflater.from(parent.context).inflate(R.layout.contacto_lista, parent, false)
        vh.setOnLongClickListener(this)
        return ContactosViewHolder(vh)
    }

    override fun getItemCount(): Int {
       return contactos.size
    }

    override fun onBindViewHolder(cvh: ContactosViewHolder, pos: Int) {
        val item = contactos[pos]
        cvh.bindContactos(item)
    }

    override fun onLongClick(view: View?): Boolean {
        if (listener != null)
            listener!!.onClick(view)

        return false
    }

    fun setItemLongClickListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    class ContactosViewHolder(viewC: View) : RecyclerView.ViewHolder(viewC), View.OnCreateContextMenuListener {

        private var nombreC: TextView = viewC.findViewById(R.id.cNombre)
        private var telefonoC: TextView = viewC.findViewById(R.id.cTelefono)

        init {
            viewC.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, p2: ContextMenu.ContextMenuInfo?) {
            if (menu != null) {
                menu.add(0,0,0,"Modificar")
                menu.add(0,1,0,"Llamar")
                menu.add(0,2,0,"Eliminar")
            }

        }


        fun bindContactos(c: Contacto) {
            nombreC.text = c.nombre
            telefonoC.text = c.telefono


        }

    }
}

