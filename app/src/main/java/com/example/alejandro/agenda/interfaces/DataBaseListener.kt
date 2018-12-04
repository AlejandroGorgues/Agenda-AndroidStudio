package com.example.alejandro.agenda.interfaces

import com.example.alejandro.agenda.AgendaBaseDatos
import com.example.alejandro.agenda.model.Contacto

interface DataBaseListener {
    //Se utilizan una serie de datos de tipo String e Int
    //Devuelve void
    fun modifiedDataContact(id:Int, nombre:String,direccion:String,movil:String,telefono:String,correo:String)

    //No se pasan datos
    //Devuelve una estructura de tipo Pair que contiene un array de enteros y un arrayList de tipo contacto
    fun returnIdentContact(): Pair<IntArray?, ArrayList<Contacto>>

    //Se pasa un dato de tipo entero
    //Devuelve un objeto de tipo Contacto
    fun searchDataContact(id:Int): Contacto

    //Se utilizan una serie de datos de tipo String e Int
    //Devuelve void
    fun createContact(nombre:String,direccion:String,movil:String,telefono:String,correo:String)

    //Se pasa un dato de tipo entero
    //Devuelve void
    fun deleteContact(id:Int)

    //No se pasa nada
    //Devuelve void
    fun exportJsonData()

    //No se pasa nada
    //Devuelve void
    fun importJsonData()

    //No se pasa nada
    //Devuelve void
    fun callContact()

    //No se pasa nada
    //Devuelve un objeto de tipo AgendaBaseDatos
    fun databaseInstance(): AgendaBaseDatos
}