package com.example.alejandro.agenda

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import java.util.regex.Pattern

class MostrarContacto : AppCompatActivity() {
    private lateinit var edNombre: EditText
    private lateinit var edDireccion: EditText
    private lateinit var edMovil: EditText
    private lateinit var edTelefono: EditText
    private lateinit var edCorreo: EditText

    private var bGrabar: Button? = null
    private var bborrar: Button? = null
    private var bCancelar: Button? = null

    private lateinit var tilNombre: TextInputLayout
    private lateinit var tilDireccion: TextInputLayout
    private lateinit var tilTelefono: TextInputLayout
    private lateinit var tilMovil: TextInputLayout
    private lateinit var tilCorreo: TextInputLayout

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

        tilNombre = findViewById(R.id.til_nombre)
        tilDireccion = findViewById(R.id.til_direccion)
        tilTelefono = findViewById(R.id.til_telefono)
        tilMovil = findViewById(R.id.til_movil)
        tilCorreo = findViewById(R.id.til_correo)

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

        edNombre.setText(nombre)
        edDireccion.setText(direccion)
        edMovil.setText(movil)
        edTelefono.setText(telefono)
        edCorreo.setText(correo)

        bborrar!!.setOnClickListener { validarDatos(2) }
        bGrabar!!.setOnClickListener { validarDatos(1) }
        bCancelar!!.setOnClickListener { validarDatos(0) }

        edNombre.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        
                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        tilNombre.error = null
                    }

                    override fun afterTextChanged(p0: Editable?) {
                        
                    }


                })


        edDireccion.addTextChangedListener(
                object : TextWatcher {

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        
                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        tilDireccion.error = null
                    }

                    override fun afterTextChanged(p0: Editable?) {
                        
                    }

                })

        edTelefono.addTextChangedListener(
                object : TextWatcher {

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        tilTelefono.error = null
                    }

                    override fun afterTextChanged(p0: Editable?) {
                    }
                })

        edMovil.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        tilMovil.error = null
                    }

                    override fun afterTextChanged(p0: Editable?) {
                    }
                })

        edCorreo.addTextChangedListener(
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
        if (!patron.matcher(nombre).matches() || nombre.length > 30) {
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

        if(valor != 0) {

            val a = esNombreValido(nombre)
            val b = esDireccionValida(direccion)
            val c = esTelefonoValido(telefono)
            val d = esTelefonoValido(movil)
            val e = esCorreoValido(correo)

            if (a && b && c && d && e) {
                devolverResultado(valor)
            }
        }else{
            devolverResultado(0)
        }
    }

    fun devolverResultado(valor: Int) {
        val i = Intent()
        i.putExtra("ID", id)
        when (valor) {
            1 -> {
                i.putExtra("Nombre", edNombre.text.toString())
                i.putExtra("Direccion", edDireccion.text.toString())
                i.putExtra("Movil", edMovil.text.toString())
                i.putExtra("Telefono", edTelefono.text.toString())
                i.putExtra("Correo", edCorreo.text.toString())
                setResult(Activity.RESULT_OK, i)

            }
            2 -> setResult(AgendaActivity.RESULT_BORRAR, i)
            else -> setResult(Activity.RESULT_CANCELED, i)
        }

        finish()


    }
}
