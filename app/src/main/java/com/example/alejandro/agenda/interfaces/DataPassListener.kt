package com.example.alejandro.agenda.interfaces

import android.os.Bundle

interface DataPassListener {
        //Se utiliza un dato de tipo Bundle y un valor numérico asociado al fragment
        //Devuelve void
        fun passData(data: Bundle, fragment:Int)
}