package com.example.alejandro.agenda

interface ContactoTouchAdapter {

    fun onMoverItem(fromPosition: Int, toPosition: Int)

    fun onEliminarItem(position: Int)
}