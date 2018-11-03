package com.example.alejandro.agenda

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText

class MostrarContacto : AppCompatActivity() {
    private var edNombre: EditText? = null
    private var edDireccion: EditText? = null
    private var edMovil: EditText? = null
    private var edTelefono: EditText? = null
    private var edCorreo: EditText? = null

    private var bGrabar: Button? = null
    private var bborrar: Button? = null
    private var bCancelar: Button? = null

    private var id: Int = 0
    private var nombre: String? = null
    private var direccion: String? = null
    private var movil: String? = null
    private var telefono: String? = null
    private var correo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mostrar_contacto)
        edNombre = findViewById(R.id.edNombre)
        edDireccion = findViewById(R.id.edDireccion)
        edMovil = findViewById(R.id.edMovil)
        edTelefono = findViewById(R.id.edTelefono)
        edCorreo = findViewById(R.id.edCorreo)

        bGrabar = findViewById(R.id.bGrabar)
        bborrar = findViewById(R.id.bBorrar)
        bCancelar = findViewById(R.id.bCancelar)

        val extras = intent.extras


        id = extras.getInt("ID")
        nombre = extras.getString("Nombre")
        direccion = extras.getString("Direccion")
        movil = extras.getString("Movil")
        telefono = extras.getString("Telefono")
        correo = extras.getString("Correo")

        edNombre!!.setText(nombre)
        edDireccion!!.setText(direccion)
        edMovil!!.setText(movil)
        edTelefono!!.setText(telefono)
        edCorreo!!.setText(correo)

        bborrar!!.setOnClickListener { devolverResultado(2) }
        bGrabar!!.setOnClickListener { devolverResultado(1) }
        bCancelar!!.setOnClickListener { devolverResultado(0) }
    }

    fun devolverResultado(valor: Int) {
        val i = Intent()
        i.putExtra("ID", id)
        when (valor) {
            1 -> {
                i.putExtra("Nombre", edNombre!!.text.toString())
                i.putExtra("Direccion", edDireccion!!.text.toString())
                i.putExtra("Movil", edMovil!!.text.toString())
                i.putExtra("Telefono", edTelefono!!.text.toString())
                i.putExtra("Correo", edCorreo!!.text.toString())
                setResult(Activity.RESULT_OK, i)

            }
            2 -> setResult(AgendaActivity.RESULT_BORRAR, i)
            else -> setResult(Activity.RESULT_CANCELED, i)
        }

        finish()


    }
}
