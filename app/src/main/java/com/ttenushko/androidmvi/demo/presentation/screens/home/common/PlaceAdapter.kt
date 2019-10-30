package com.ttenushko.androidmvi.demo.presentation.screens.home.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ttenushko.androidmvi.demo.R
import com.ttenushko.androidmvi.demo.domain.weather.model.Place
import kotlinx.android.synthetic.main.item_place.view.*

class PlaceAdapter(
    ctx: Context,
    private val callback: Callback
) : RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(ctx)
    private var items: List<Place> = listOf()

    fun set(items: List<Place>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun clear() {
        if (this.items.isNotEmpty()) {
            this.items = listOf()
            notifyDataSetChanged()
        }
    }

    fun getItemAt(position: Int): Place =
        items[position]

    override fun getItemCount(): Int =
        items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.item_place, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = items[position]
        holder.bind(place)
    }

    private fun handleItemClicked(position: Int) {
        callback.onItemClicked(items[position])
    }

    inner class ViewHolder(
        rootView: View
    ) : RecyclerView.ViewHolder(rootView) {

        init {
            rootView.setOnClickListener {
                adapterPosition.let { position ->
                    if (RecyclerView.NO_POSITION != position) {
                        handleItemClicked(position)
                    }
                }
            }
        }

        fun bind(place: Place) {
            itemView.placeTitle.text =
                "${place.name}, ${place.countyCode.toUpperCase()}"
            itemView.placeLocation.text =
                "%.6f, %.6f".format(place.location.latitude, place.location.longitude)
        }
    }

    interface Callback {
        fun onItemClicked(place: Place)
    }
}