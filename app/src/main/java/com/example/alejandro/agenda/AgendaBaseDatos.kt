package com.example.alejandro.agenda

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class AgendaBaseDatos(context: Context) : SQLiteOpenHelper(context, NOMBRE_DB, null, VERSION_DB) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(TABLA_CONTACTOS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {


        db.execSQL("DROP TABLE IF EXISTS$TABLA_CONTACTOS")
        onCreate(db)
    }


    fun insertarContacto(nombre: String, direccion: String, movil:String, telefono:String, correo:String) {

        val db = writableDatabase
        db?.execSQL("INSERT OR IGNORE INTO contactos(nombre,direccion,movil,telefono,correo) VALUES (nombre,direccion,movil,telefono,correo) ")

        db!!.close()
    }

    fun modificarContacto(id: Int, nombre: String, direccion: String, movil:String, telefono:String, correo:String) {
        val db = writableDatabase
        val valores = ContentValues()
        valores.put("nombre", nombre)
        valores.put("direccion", direccion)
        valores.put("movil", movil)
        valores.put("telefono", telefono)
        valores.put("correo", correo)

        db.update("contactos", valores, "_id=$id", null)
        db.close()

    }

    fun borrarContacto(id: Int) {
        val db = writableDatabase
        db.delete("notas", "_id=$id", null)
        db.close()
        /* como en insert se puede utilizar db.execSQL de la sigueinte manera
        Eliminar un registro con execSQL(), utilizando argumentos
        String[] args = new String[]{ String.valueOf(id);};
         db.execSQL("DELETE FROM notas WHERE _id=?", args);
         */
    }

    fun buscarContacto(id: Int): Contacto {
        val db = readableDatabase
        val valoresRecuperar = arrayOf("_id", "nombre", "direccion", "movil", "telefono", "correo")
        val c = db.query("notas", valoresRecuperar, "_id=$id", null, null, null, null, null)
        c?.moveToFirst()
        val contacto = Contacto(c!!.getInt(0), c.getString(1), c.getString(2), c.getString(2),c.getString(2),c.getString(2))
        db.close()
        c.close()
        return contacto
    }


    fun buscarContactoCursor(): Cursor {
        val db = readableDatabase
        val valoresRecuperar = arrayOf("_id", "nombre", "direccion", "movil", "telefono", "correo")


        return db.query("notas", valoresRecuperar, null, null, null, null, null, null)

    }

    fun numerodeFilas(): Int {
        return DatabaseUtils.queryNumEntries(writableDatabase, "contactos").toInt()
    }


    fun recuperaIds(): IntArray {
        val datosId: IntArray
        var i: Int
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT _id FROM contactos", null)

        if (cursor.count > 0) {
            datosId = IntArray(cursor.count)
            i = 0
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                datosId[i] = cursor.getInt(0)
                i++
                cursor.moveToNext()
            }
        } else
            datosId = IntArray(0)
        cursor.close()
        return datosId
    }

    companion object {


        private const val VERSION_DB = 1
        private const val NOMBRE_DB = "contactosDB.db"
        private const val TABLA_CONTACTOS = "CREATE TABLE contactos " +
                "(_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                " nombre VARCHAR(100) , direccion VARCHAR(100), movil VARCHAR(10), telefono VARCHAR(10), correo VARCHAR(100) )"
    }
}
