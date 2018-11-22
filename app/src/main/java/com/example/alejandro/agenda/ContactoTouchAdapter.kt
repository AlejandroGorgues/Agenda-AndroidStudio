package com.example.alejandro.agenda

interface ContactoTouchAdapter {

    abstract fun onMoverItem(fromPosition: Int, toPosition: Int)

    abstract fun onEliminarItem(position: Int)
}