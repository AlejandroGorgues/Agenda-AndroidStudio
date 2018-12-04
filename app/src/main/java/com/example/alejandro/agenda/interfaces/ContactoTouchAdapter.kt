package com.example.alejandro.agenda.interfaces

interface ContactoTouchAdapter {

    //Se pasan dos datos de tipo entero
    //Devuelve void
    fun onMoverItem(fromPosition: Int, toPosition: Int)

    //Se un dato de tipo entero
    //Devuelve void
    fun onEliminarItem(position: Int)
}