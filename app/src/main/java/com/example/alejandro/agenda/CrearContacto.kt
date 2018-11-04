package com.example.alejandro.agenda

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.inputmethodservice.Keyboard
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText


class CrearContacto : AppCompatActivity() {
    private var nombre: EditText? = null
    private var direccion: EditText? = null
    private var movil: EditText? = null
    private var telefono: EditText? = null
    private var correo: EditText? = null
    private var bCrear: Button? = null
    private var bCancelar: Button? = null
    internal var id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        setContentView(R.layout.activity_crear_contacto)
        nombre = findViewById(R.id.nombre)
        direccion = findViewById(R.id.direccion)
        movil = findViewById(R.id.movil)
        telefono = findViewById(R.id.telefono)
        correo = findViewById(R.id.correo)

        bCrear = findViewById(R.id.bCrear)
        bCancelar = findViewById(R.id.bCancelar)

        bCancelar!!.setOnClickListener { devolverResultado(2) }
        bCrear!!.setOnClickListener { devolverResultado(1) }
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
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
