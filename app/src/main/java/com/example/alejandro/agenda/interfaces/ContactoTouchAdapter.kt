package com.example.alejandro.agenda.interfaces

interface ContactoTouchAdapter {

    fun onMoverItem(fromPosition: Int, toPosition: Int)

    fun onEliminarItem(position: Int)
}