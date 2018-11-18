package com.example.alejandro.agenda

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import org.json.JSONArray
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.regex.Pattern


class CrearContacto : AppCompatActivity() {
    private lateinit var nombre: EditText
    private lateinit var direccion: EditText
    private lateinit var movil: EditText
    private lateinit var telefono: EditText
    private lateinit var correo: EditText

    private lateinit var tilNombre: TextInputLayout
    private lateinit var tilDireccion: TextInputLayout
    private lateinit var tilTelefono: TextInputLayout
    private lateinit var tilMovil: TextInputLayout
    private lateinit var tilCorreo: TextInputLayout

    internal var id: Int = 0

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        setContentView(R.layout.activity_crear_contacto)

        toolbar = findViewById(R.id.crearCToolbar)
        setSupportActionBar(toolbar)

        nombre = findViewById(R.id.crNombre)
        direccion = findViewById(R.id.crDireccion)
        movil = findViewById(R.id.crMovil)
        telefono = findViewById(R.id.crTelefono)
        correo = findViewById(R.id.crCorreo)

        tilNombre = findViewById(R.id.til_nombre)
        tilDireccion = findViewById(R.id.til_direccion)
        tilTelefono = findViewById(R.id.til_telefono)
        tilMovil = findViewById(R.id.til_movil)
        tilCorreo = findViewById(R.id.til_correo)

        nombre.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        
                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        tilNombre.error = null
                    }

                    override fun afterTextChanged(p0: Editable?) {
                        
                    }

                    
                })


        direccion.addTextChangedListener(
                object : TextWatcher {

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        
                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        tilDireccion.error = null
                    }

                    override fun afterTextChanged(p0: Editable?) {
                        
                    }

                })

        telefono.addTextChangedListener(
                object : TextWatcher {

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        
                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        tilTelefono.error = null
                    }

                    override fun afterTextChanged(p0: Editable?) {
                        
                    }
                })

        movil.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        
                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        tilMovil.error = null
                    }

                    override fun afterTextChanged(p0: Editable?) {
                        
                    }
                })

        correo.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        
                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        esCorreoValido(s.toString())
                    }

                    override fun afterTextChanged(p0: Editable?) {
                        
                    }
                })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_crear_contacto, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.bAction_crearC-> {
                validarDatos(1)
                true
            }
            R.id.bAction_cancelarCrC -> {
                validarDatos(2)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun esNombreValido(nombre: String): Boolean {
        val patron = Pattern.compile("^[a-zA-Z ]+$")
        if (!patron.matcher(nombre).matches() || nombre.length > 30) {
            tilNombre.error = "Nombre inválido"
            return false
        } else {
            tilNombre.error = null
        }

        return true
    }

    private fun esDireccionValida(nombre: String): Boolean {
        val patron = Pattern.compile("^[a-zA-Z0-9]+$")
        if (!patron.matcher(nombre).matches() || nombre.length > 50) {
            tilNombre.error = "Nombre inválido"
            return false
        } else {
            tilNombre.error = null
        }

        return true
    }

    private fun esTelefonoValido(telefono: String): Boolean {
        if (!Patterns.PHONE.matcher(telefono).matches()) {
            tilTelefono.error = "Teléfono inválido"
            return false
        } else {
            tilTelefono.error = null
        }

        return true
    }

    private fun esCorreoValido(correo: String): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            tilCorreo.error = "Correo electrónico inválido"
            return false
        } else {
            tilCorreo.error = null
        }

        return true
    }

    private fun validarDatos(valor: Int) {
            val nombre = tilNombre.editText!!.text.toString()
            val direccion = tilDireccion.editText!!.text.toString()
            val telefono = tilTelefono.editText!!.text.toString()
            val movil = tilMovil.editText!!.text.toString()
            val correo = tilCorreo.editText!!.text.toString()

            val a = esNombreValido(nombre)
            val b = esDireccionValida(direccion)
            val c = esTelefonoValido(telefono)
            val d = esTelefonoValido(movil)
            val e = esCorreoValido(correo)

            if (a && b && c && d && e) {
                devolverResultado(valor)
            }
    }

        //Crea un intent el cual pasa una serie de valores de vuelta a la actividad principal
        fun devolverResultado(valor: Int) {
            val i = Intent()
            if (valor == 1) {
                setResult(Activity.RESULT_OK, i)
                i.putExtra("Nombre", nombre.text.toString())
                i.putExtra("Direccion", direccion.text.toString())
                i.putExtra("Movil", movil.text.toString())
                i.putExtra("Telefono", telefono.text.toString())
                i.putExtra("Correo", correo.text.toString())
            } else
                setResult(Activity.RESULT_CANCELED, i)

            finish()


        }
}
