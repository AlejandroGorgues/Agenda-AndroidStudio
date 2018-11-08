package com.example.alejandro.agenda

import android.content.Context
import android.database.Cursor
import android.support.v4.widget.CursorAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class AgendaAdapter(contexto: Context, c: Cursor) : CursorAdapter(contexto, c, false) {
    private var inflater: LayoutInflater? = null
    private lateinit var nombre: TextView
    private lateinit var telefono: TextView

    override fun newView(contexto: Context, c: Cursor, padre: ViewGroup): View {
        inflater = contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater!!.inflate(R.layout.contacto_lista, padre, false)
    }

    override fun bindView(vista: View, contexto: Context, c: Cursor) {
        nombre = vista.findViewById(R.id.cNombre) as TextView
        telefono = vista.findViewById(R.id.cTelefono) as TextView
        nombre.text = c.getString(c.getColumnIndex("nombre"))
        telefono.text = c.getString(c.getColumnIndex("telefono"))

    }

}
