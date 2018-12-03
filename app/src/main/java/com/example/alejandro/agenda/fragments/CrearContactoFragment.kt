package com.example.alejandro.agenda.fragments


import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.*
import android.widget.EditText
import com.example.alejandro.agenda.AgendaActivity
import com.example.alejandro.agenda.interfaces.DataBaseListener
import com.example.alejandro.agenda.interfaces.DataPassListener
import com.example.alejandro.agenda.R
import java.util.regex.Pattern


class CrearContactoFragment : Fragment() {


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

    private lateinit var activityDataBaseListener: DataBaseListener
    private lateinit var activityPassData: DataPassListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_crear_contacto, container, false)

        activityDataBaseListener = activity as DataBaseListener
        activityPassData = activity as DataPassListener



        toolbar = view.findViewById(R.id.crearCToolbar)
        (activity as AgendaActivity).setSupportActionBar(toolbar)

        nombre = view.findViewById(R.id.crNombre)
        direccion = view.findViewById(R.id.crDireccion)
        movil = view.findViewById(R.id.crMovil)
        telefono = view.findViewById(R.id.crTelefono)
        correo = view.findViewById(R.id.crCorreo)

        tilNombre = view.findViewById(R.id.til_nombre)
        tilDireccion = view.findViewById(R.id.til_direccion)
        tilTelefono = view.findViewById(R.id.til_telefono)
        tilMovil = view.findViewById(R.id.til_movil)
        tilCorreo = view.findViewById(R.id.til_correo)



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

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        activity!!.menuInflater.inflate(R.menu.menu_crear_contacto, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.bAction_crearC -> {
                validarDatos(1)
                true
            }
            R.id.bAction_cancelarCrC -> {
                validarDatos(0)

                true
            }
            else -> super.onOptionsItemSelected(item)
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
        if (!patron.matcher(nombre).matches() || nombre.length > 50) {
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


        if (valor == 1) {


            activityDataBaseListener.createContact(nombre.text.toString(),direccion.text.toString(),movil.text.toString(),telefono.text.toString(),correo.text.toString())
            vaciarCampos()
            activityPassData.passData(bundle, 0)

        } else
            vaciarCampos()
            activityPassData.passData(bundle, 0)
    }

    fun vaciarCampos(){
        tilNombre.editText!!.text = Editable.Factory.getInstance().newEditable("")
        tilDireccion.editText!!.text = Editable.Factory.getInstance().newEditable("")
        tilTelefono.editText!!.text = Editable.Factory.getInstance().newEditable("")
        tilMovil.editText!!.text = Editable.Factory.getInstance().newEditable("")
        tilCorreo.editText!!.text = Editable.Factory.getInstance().newEditable("")
    }

}
