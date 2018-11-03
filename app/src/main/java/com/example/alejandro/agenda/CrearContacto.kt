package com.example.alejandro.agenda

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText


class CrearContacto : AppCompatActivity() {
    private var nombre: EditText? = null
    private var direccion: EditText? = null
    private var movil: EditText? = null
    private var telefono: EditText? = null
    private var correo: EditText? = null
    private var bGrabar: Button? = null
    private var bCancelar: Button? = null
    internal var id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_contacto)
        nombre = findViewById(R.id.nombre)
        direccion = findViewById(R.id.direccion)
        movil = findViewById(R.id.movil)
        telefono = findViewById(R.id.telefono)
        correo = findViewById(R.id.correo)

        bGrabar = findViewById(R.id.bGrabar)
        bCancelar = findViewById(R.id.bBorrar)

        bCancelar!!.setOnClickListener { devolverResultado(2) }
        bGrabar!!.setOnClickListener { devolverResultado(1) }
    }

    //Crea un intent el cual pasa una serie de valores de vuelta a la actividad principal
    fun devolverResultado(valor: Int) {
        val i = Intent()
        if (valor == 1) {
            setResult(Activity.RESULT_OK, i)
            i.putExtra("Nombre", nombre!!.text.toString())
            i.putExtra("Direccion", direccion!!.text.toString())
            i.putExtra("Movil", movil!!.text.toString())
            i.putExtra("Telefono", telefono!!.text.toString())
            i.putExtra("Correo", correo!!.text.toString())
        } else
            setResult(Activity.RESULT_CANCELED, i)

        finish()


    }
}
