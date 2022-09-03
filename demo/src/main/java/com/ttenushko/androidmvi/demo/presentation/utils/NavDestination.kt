package com.ttenushko.androidmvi.demo.presentation.utils

import androidx.collection.SparseArrayCompat
import androidx.navigation.NavAction
import androidx.navigation.NavDestination

@Suppress("UNCHECKED_CAST")
val NavDestination.actions: Map<Int, NavAction>?
    get() =
        try {
            this.findField { "mActions" == it.name }?.let {
                val map = HashMap<Int, NavAction>()
                val isAccessible = it.isAccessible
                it.isAccessible = true
                (it.get(this) as SparseArrayCompat<NavAction>).let {
                    for (i in 0 until it.size()) {
                        val key = it.keyAt(i)
                        val value = it.get(key)!!
                        map[key] = value
                    }
                }
                it.isAccessible = isAccessible
                map
            }
        } catch (error: Throwable) {
            null
        }

fun NavDestination.getActionIdByDestinationIdOrThrow(destinationId: Int): Int =
    actions?.getActionIdByDestinationId(destinationId)
        ?: throw IllegalArgumentException("There is no action for specified destination id: $destinationId")

fun NavDestination.getActionIdByDestinationId(destinationId: Int): Int? =
    actions?.getActionIdByDestinationId(destinationId)

fun Map<Int, NavAction>.getActionIdByDestinationId(destinationId: Int): Int? =
    entries.firstOrNull { it.value.destinationId == destinationId }?.key