package com.remindersample.adapter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.remindersample.BR
import com.remindersample.R
import com.remindersample.databinding.ListItemEventBinding
import com.remindersample.models.Event

class EventsAdapter(private var events: ArrayList<Event>, private var callback: EventsCallback) :
    RecyclerView.Adapter<EventsAdapter.Holder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Holder {
        return Holder(DataBindingUtil.inflate(LayoutInflater.from(p0.context), R.layout.list_item_event, p0, false))
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(p0: Holder, p1: Int) {
        p0.bind(events[p0.adapterPosition], callback)
    }


    class Holder(private var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event, callback: EventsCallback) {
            binding.setVariable(BR.obj, event)
            binding.executePendingBindings()

            binding.root.setOnClickListener {
                callback.viewEvent(event.id)
            }

            val bindingEvent: ListItemEventBinding = binding as ListItemEventBinding
            bindingEvent.delete.setOnClickListener {
                callback.deleteEvent(event.id)
            }
        }
    }

    fun updateList(events: ArrayList<Event>) {
        this.events = events
        notifyDataSetChanged()
    }

    interface EventsCallback {
        fun deleteEvent(id: String)
        fun viewEvent(id: String)
    }
}