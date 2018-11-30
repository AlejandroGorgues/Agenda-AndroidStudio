package com.example.alejandro.agenda

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.*
import android.widget.EditText
import java.util.regex.Pattern




class MostrarContactoFragment : Fragment() {

    private lateinit var edNombre: EditText
    private lateinit var edDireccion: EditText
    private lateinit var edMovil: EditText
    private lateinit var edTelefono: EditText
    private lateinit var edCorreo: EditText

    private lateinit var tilNombre: TextInputLayout
    private lateinit var tilDireccion: TextInputLayout
    private lateinit var tilTelefono: TextInputLayout
    private lateinit var tilMovil: TextInputLayout
    private lateinit var tilCorreo: TextInputLayout

    private var idC: Int = 0
    private var nombre: String? = null
    private var direccion: String? = null
    private var movil: String? = null
    private var telefono: String? = null
    private var correo: String? = null


    private lateinit var activityDataBaseListener: DataBaseListener
    private lateinit var activityPassData: DataPassListener

    private lateinit var toolbar: Toolbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_mostrar_contacto, container, false)

        activityDataBaseListener = activity as DataBaseListener
        activityPassData = activity as DataPassListener

        toolbar = view.findViewById(R.id.modificarCToolbar)
        (activity as AgendaActivity).setSupportActionBar(toolbar)

        edNombre = view.findViewById(R.id.edNombre)
        edDireccion = view.findViewById(R.id.edDireccion)
        edMovil = view.findViewById(R.id.edMovil)
        edTelefono = view.findViewById(R.id.edTelefono)
        edCorreo = view.findViewById(R.id.edCorreo)

        tilNombre = view.findViewById(R.id.til_nombre)
        tilDireccion = view.findViewById(R.id.til_direccion)
        tilTelefono = view.findViewById(R.id.til_telefono)
        tilMovil = view.findViewById(R.id.til_movil)
        tilCorreo = view.findViewById(R.id.til_correo)



        val bundle = this.arguments
        if (bundle != null) {


            idC = bundle.getInt("ID")
            nombre = bundle.getString("Nombre")
            direccion = bundle.getString("Direccion")
            movil = bundle.getString("Movil")
            telefono = bundle.getString("Telefono")
            correo = bundle.getString("Correo")
        }


        edNombre.setText(nombre)
        edDireccion.setText(direccion)
        edMovil.setText(movil)
        edTelefono.setText(telefono)
        edCorreo.setText(correo)

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

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        activity!!.menuInflater.inflate(R.menu.menu_modificar_contacto, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.bAction_modC-> {
                validarDatos(1)
                return true
            }
            R.id.bAction_llamarC -> {
                activityDataBaseListener.callContact()
                return true
            }
            R.id.bAction_cancelarModC -> {
                validarDatos(0)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun esNombreValido(nombre: String): Boolean {
        val patron = Pattern.compile("^[a-zA-Z ]+$")
        if (!patron.matcher(nombre).matches() || nombre.length > 30) {
            tilNombre.error = R.string.nombreInvalido.toString()
            return false
        } else {
            tilNombre.error = null
        }

        return true
    }

    private fun esDireccionValida(nombre: String): Boolean {
        val patron = Pattern.compile("^[a-zA-Z0-9]+$")
        if (!patron.matcher(nombre).matches() || nombre.length > 30) {
            tilNombre.error = R.string.direccionInvalido.toString()
            return false
        } else {
            tilNombre.error = null
        }

        return true
    }

    private fun esTelefonoValido(telefono: String): Boolean {
        if (!Patterns.PHONE.matcher(telefono).matches()) {
            tilTelefono.error = R.string.telefonoInvalido.toString()
            return false
        } else {
            tilTelefono.error = null
        }

        return true
    }

    private fun esCorreoValido(correo: String): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            tilCorreo.error = R.string.correoInvalido.toString()
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
        val bundle = Bundle()


        when (valor) {
            1 -> {
                activityDataBaseListener.modifiedDataContact(idC, edNombre.text.toString(), edDireccion.text.toString(), edMovil.text.toString(), edTelefono.text.toString(), edCorreo.text.toString())
                activityPassData.passData(bundle, 0)
            }
            else ->{
                activityPassData.passData(bundle, 0)
            }
        }
    }



}
