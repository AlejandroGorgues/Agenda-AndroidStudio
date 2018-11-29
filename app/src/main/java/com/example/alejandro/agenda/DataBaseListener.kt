package com.example.alejandro.agenda

import org.json.JSONArray

interface DataBaseListener {
    fun modifiedDataContact(id:Int, nombre:String,direccion:String,movil:String,telefono:String,correo:String)
    fun returnIdentContact(): Pair<IntArray?, ArrayList<Contacto>>
    fun searchDataContact(id:Int): Contacto
    fun createContact(nombre:String,direccion:String,movil:String,telefono:String,correo:String)
    fun deleteContact(id:Int)
    fun getJsonData(): JSONArray
    fun databaseInstance(): AgendaBaseDatos
}