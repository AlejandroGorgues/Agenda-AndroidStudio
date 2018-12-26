package com.example.alejandro.agenda

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import android.view.LayoutInflater
import com.example.alejandro.agenda.interfaces.ContactoTouchAdapter
import com.example.alejandro.agenda.model.Contacto
import java.util.*
import android.graphics.drawable.GradientDrawable
import android.widget.Filter
import android.widget.Filterable
import com.example.alejandro.agenda.interfaces.ReloadDataAdapter
import kotlin.collections.ArrayList




class AgendaAdapter(private var contactos : ArrayList<Contacto>, private val agendaDB: AgendaBaseDatos, private val context: Context, private val reloadList: ReloadDataAdapter) : RecyclerView.Adapter<AgendaAdapter.ContactosViewHolder>(), ContactoTouchAdapter, View.OnClickListener, Filterable {



    private var listener: View.OnClickListener? = null
    private var contactosFiltered: ArrayList<Contacto> = contactos
    private var contactosAux: ArrayList<Contacto> = ArrayList()
    private var edited = false


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
        cvh.bindContactos(item, context)
    }

    override fun onClick(view: View?) {
        if (listener != null)
            listener!!.onClick(view)
    }

    fun setOnClickListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    fun getContactosAux(): Pair<ArrayList<Contacto>, Boolean>{
        return Pair(contactosAux, edited)
    }

    //A partir de la posición inicial del objeto que se quiere mover, y adonde se quiere mover,
    //se modifica el array de Contactos y la base de datos
    override fun onMoverItem(fromPosition: Int, toPosition: Int) {
        val contactoAux = contactos[fromPosition]

        //Se llama al método que permite modificar los datos de la base de datos para intercambiarlos
        agendaDB.modificarContacto(contactos[fromPosition].id, contactos[toPosition].nombre!!, contactos[toPosition].direccion!!, contactos[toPosition].movil!!, contactos[toPosition].telefono!!, contactos[toPosition].correo!!)
        agendaDB.modificarContacto(contactos[toPosition].id, contactoAux.nombre!!, contactoAux.direccion!!, contactoAux.movil!!, contactoAux.telefono!!, contactoAux.correo!!)

        //Intercambia las posiciones de los contactos en el array
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
        reloadList.reloadListAdapter()
        notifyItemRemoved(position)

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()
                contactos = contactosFiltered
                contactos = if (charString.isEmpty()) {
                    contactos
                } else {
                    val filteredList = ArrayList<Contacto>()
                    for (row in contactos) {

                        // name match condition. this might differ depending on your requirement
                        if (row.nombre!!.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }

                    filteredList
                }

                val filterResults = Filter.FilterResults()
                filterResults.values = contactos
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                contactos = filterResults.values as ArrayList<Contacto>
                contactosAux = filterResults.values as ArrayList<Contacto>
                edited = contactos.size != contactosFiltered.size
                notifyDataSetChanged()
            }
        }
    }



    class ContactosViewHolder (viewC: View ) : RecyclerView.ViewHolder(viewC) {

        private var nombreC: TextView = viewC.findViewById(R.id.cNombre)
        private var telefonoC: TextView = viewC.findViewById(R.id.cTelefono)
        private var circuloView: TextView = viewC.findViewById(R.id.circleView)

        fun bindContactos(c: Contacto, context: Context) {

            val androidColors =   context.resources.getIntArray(R.array.agendaColors)
            val randomAndroidColor = androidColors[Random().nextInt(androidColors.size)]

            val drawable = circuloView.background as GradientDrawable
            drawable.setColor(randomAndroidColor)

            circuloView.text = c.nombre!![0].toString().toUpperCase()
            nombreC.text = c.nombre
            telefonoC.text = c.telefono

        }

    }
}

