package com.example.alejandro.agenda.interfaces

import com.example.alejandro.agenda.AgendaBaseDatos
import com.example.alejandro.agenda.model.Contacto

interface DataBaseListener {
    fun modifiedDataContact(id:Int, nombre:String,direccion:String,movil:String,telefono:String,correo:String)
    fun returnIdentContact(): Pair<IntArray?, ArrayList<Contacto>>
    fun searchDataContact(id:Int): Contacto
    fun createContact(nombre:String,direccion:String,movil:String,telefono:String,correo:String)
    fun deleteContact(id:Int)
    fun exportJsonData()
    fun importJsonData()
    fun callContact()
    fun databaseInstance(): AgendaBaseDatos
}