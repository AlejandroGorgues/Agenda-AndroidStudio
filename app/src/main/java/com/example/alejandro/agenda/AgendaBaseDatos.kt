package com.example.alejandro.agenda

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import org.json.JSONObject
import org.json.JSONArray


class AgendaBaseDatos(context: Context) : SQLiteOpenHelper(context, NOMBRE_DB, null, VERSION_DB) {


    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(TABLA_CONTACTOS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {


        db.execSQL("DROP TABLE IF EXISTS$TABLA_CONTACTOS")
        onCreate(db)
    }


    fun insertarContacto(id: Int, nombre: String, direccion: String, movil:String, telefono:String, correo:String) {

        val db = this.writableDatabase
        val values = ContentValues()
        values.put("_id", id)
        values.put("nombre", nombre)
        values.put("direccion", direccion)
        values.put("movil", movil)
        values.put("telefono", telefono)
        values.put("correo", correo)

        db.insert("contactos", null, values)

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
        db.delete("contactos", "_id=$id", null)
        db.close()
    }

    fun buscarContacto(id: Int): Contacto {
        val db = readableDatabase
        val valoresRecuperar = arrayOf("_id", "nombre", "direccion", "movil", "telefono", "correo")
        val c = db.query("contactos", valoresRecuperar, "_id=$id", null, null, null, null, null)
        c?.moveToFirst()
        val contacto = Contacto(c!!.getInt(0), c.getString(1), c.getString(2), c.getString(3),c.getString(4),c.getString(5))
        db.close()
        c.close()
        return contacto
    }


    fun buscarContactoCursor(): Cursor {
        val db = readableDatabase
        val valoresRecuperar = arrayOf("_id", "nombre", "direccion", "movil", "telefono", "correo")


        return db.query("contactos", valoresRecuperar, null, null, null, null, null, null)

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

    fun getJson(): JSONArray {

        val db = readableDatabase


        val searchQuery = "SELECT  * FROM contactos"
        val cursor = db.rawQuery(searchQuery, null)

        val resultSet = JSONArray()

        cursor.moveToFirst()
        while (!cursor.isAfterLast) {

            val totalColumn = cursor.columnCount
            val rowObject = JSONObject()

            for (i in 0 until totalColumn) {
                if (cursor.getColumnName(i) != null) {

                    try {

                        if (cursor.getString(i) != null) {
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i))
                        } else {
                            rowObject.put(cursor.getColumnName(i), "")
                        }
                    } catch (e: Exception) {
                    }

                }

            }

            resultSet.put(rowObject)
            cursor.moveToNext()
        }

        cursor.close()
        return resultSet
    }

    companion object {

        private const val VERSION_DB = 1
        private const val NOMBRE_DB = "contactosDB.db"
        private const val TABLA_CONTACTOS = "CREATE TABLE contactos " +
                "(_id INTEGER NOT NULL PRIMARY KEY," +
                " nombre VARCHAR(100) , direccion VARCHAR(100), movil VARCHAR(10), telefono VARCHAR(10), correo VARCHAR(100) )"
    }
}
