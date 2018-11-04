package com.example.alejandro.agenda

import android.content.Context
import android.database.Cursor
import android.support.v4.widget.CursorAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class AgendaAdapter(contexto: Context, c: Cursor) : CursorAdapter(contexto, c, false) {
    private var inflater: LayoutInflater? = null // Crea Layouts a partir del XML
    private lateinit var nombre: TextView
    private lateinit var telefono: TextView

    /* bindView() es el encargado de poblar la lista con los datos del cursor y
       newView() es quien infla cada view de la lista. Al implementar ambos
       métodos no debemos preocuparnos por iterar el curso, esto es manejado internamente.
      */
    override/* en newView() accedemos a la instancia del LayoutInflater a través del Context
      y luego invocamos inflate() para inflar nuestra fila.*/ fun newView(contexto: Context, c: Cursor, padre: ViewGroup): View {
        inflater = contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater!!.inflate(R.layout.contacto_lista, padre, false)
    }

    //TODO ESTA REGULAR, HAY QUE MIERAR BIEN SI FUNCIONA LO DEL CNOMBRE CON LA ID Y ESO
    override/* En bindView() simplemente debemos obtener los valores de las columnas
     y emparejarlos en los TextViews del layout.*/ fun bindView(vista: View, contexto: Context, c: Cursor) {
        nombre = vista.findViewById(R.id.cNombre) as TextView
        telefono = vista.findViewById(R.id.cTelefono) as TextView
        nombre.text = c.getString(c.getColumnIndex("nombre"))
        telefono.text = c.getString(c.getColumnIndex("telefono"))

    }
}
