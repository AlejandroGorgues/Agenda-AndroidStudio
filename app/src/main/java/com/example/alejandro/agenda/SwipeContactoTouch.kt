package com.example.alejandro.agenda

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper


class SwipeContactoTouch(private val contactoTouchAdapter: ContactoTouchAdapter) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        // direccion en la que un elemento puede ser arrastrado
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        // direccion en la que un elemento puede hacer Swipe
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder): Boolean {
        contactoTouchAdapter.onMoverItem(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        contactoTouchAdapter.onEliminarItem(viewHolder.adapterPosition)
    }

    override// Define la velocidad máxima que ItemTouchHelper calculará para los movimientos del puntero.
    fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return defaultValue / 4f
    }

    override//Define la velocidad mínima a considerar como una acción de deslizamiento.
    fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return defaultValue * 300
    }
}