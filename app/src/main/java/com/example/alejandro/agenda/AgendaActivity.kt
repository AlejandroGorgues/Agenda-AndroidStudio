package com.example.alejandro.agenda

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build

import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.*
import android.support.v7.widget.Toolbar
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.PopupMenu
import es.upm.etsisi.mirecyclerview.SwipeContactoTouch

import org.json.JSONArray
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter

class AgendaActivity : AppCompatActivity(), AgendaFragment.DataPassListener, FragmentManager.OnBackStackChangedListener {

    private var fragmentActual: Fragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)

        val  manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        val fragmentLista = AgendaFragment()
        fragmentActual = fragmentLista
        transaction.add(R.id.agendaActivityLayout, fragmentLista,  "fragmentPrincipal")
        transaction.addToBackStack("fragmentPrincipal")
        transaction.commit()

    }


    override fun passData(data: Bundle, fragment: Int) {
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()

        when (fragment) {
            0 -> {
                manager.addOnBackStackChangedListener(this)
                val fragmentReplace = AgendaFragment()
                fragmentActual = fragmentReplace
                fragmentReplace.arguments = data
                transaction.replace(R.id.agendaActivityLayout, fragmentReplace, "fragmentPrincipal")
                transaction.addToBackStack("fragmentPrincipal")
                transaction.commit()
                manager.addOnBackStackChangedListener(this)
            }
            1 -> {
                manager.addOnBackStackChangedListener(this)
                val fragmentReplace = CrearContactoFragment()
                fragmentActual = fragmentReplace
                fragmentReplace.arguments = data
                transaction.replace(R.id.agendaActivityLayout, fragmentReplace, "fragmentCrear")
                transaction.addToBackStack("fragmentCrear")
                transaction.commit()
                manager.addOnBackStackChangedListener(this)
            }
            else -> {
                manager.addOnBackStackChangedListener(this)
                val fragmentReplace = MostrarContactoFragment()
                fragmentReplace.arguments = data
                fragmentActual = fragmentReplace
                transaction.replace(R.id.agendaActivityLayout, fragmentReplace, "fragmentMostrar")
                transaction.addToBackStack("fragmentMostrar")
                transaction.commit()

            }
        }
    }

    override fun onBackStackChanged() {
         if (supportFragmentManager.backStackEntryCount > 0) {
            val currentFragmentTag = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name

             if (currentFragmentTag == "fragmentPrincipal"){
                 val fragmentTagLast = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 2).name

                 if(fragmentTagLast ==  "fragmentMostrar"){
                     val actualFragment = supportFragmentManager.findFragmentByTag(currentFragmentTag) as AgendaFragment
                     val bundle = actualFragment.arguments!!

                     actualFragment.onReturnFromBackStack(13, Activity.RESULT_OK, bundle)

                 }else if(fragmentTagLast == "fragmentCrear"){
                     val actualFragment = supportFragmentManager.findFragmentByTag(currentFragmentTag) as AgendaFragment
                     val bundle = actualFragment.arguments!!

                     actualFragment.onReturnFromBackStack(12, Activity.RESULT_OK, bundle)
                 }
             }
        }
    }
}
