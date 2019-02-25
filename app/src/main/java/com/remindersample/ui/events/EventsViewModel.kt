package com.remindersample.ui.events

import android.Manifest
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.*
import android.database.Cursor
import android.provider.CalendarContract
import android.support.v4.content.LocalBroadcastManager
import com.remindersample.Constants
import com.remindersample.MyApp
import com.remindersample.models.Event
import pub.devrel.easypermissions.EasyPermissions

class EventsViewModel(application: Application) : AndroidViewModel(application) {

    val events: MutableLiveData<ArrayList<Event>> = MutableLiveData()


    /**
     * listen for broadcast manager to get the events data
     */
    fun init() {
        val filter = IntentFilter()
        filter.addAction(Constants.MY_BROADCAST)
        LocalBroadcastManager.getInstance(getApplication()).registerReceiver(receiver, filter)
    }

    /**
     * get the list of events
     */
    fun getEvents() {
        if (hasPermission()) {
            println(">>>>Getting the list of Events")
            val list: ArrayList<Event> = ArrayList()
            val cur: Cursor?
            val cr: ContentResolver = getApplication<MyApp>().contentResolver

            val mProjection = arrayOf(
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events._ID
            )

            val uri = CalendarContract.Events.CONTENT_URI
            val selection =
                CalendarContract.Events.CUSTOM_APP_PACKAGE + " = ? AND " + CalendarContract.Events.DELETED + " = ? "
            val selectionArgs = arrayOf(getApplication<MyApp>().packageName, "0")

            cur = cr.query(uri, mProjection, selection, selectionArgs, null)

            while (cur!!.moveToNext()) {
                val event = Event()

                event.title = cur.getString(cur.getColumnIndex(CalendarContract.Events.TITLE))
                event.desc = cur.getString(cur.getColumnIndex(CalendarContract.Events.DESCRIPTION))
                event.id = cur.getString(cur.getColumnIndex(CalendarContract.Events._ID))

                list.add(event)
            }
            cur.close()

            events.postValue(list)
        }
    }

    /**
     * receiver to listen for new calendar events
     */
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            getEvents()
        }
    }

    /**
     * function to check if we have the permission to perfom the action or not
     */
    private fun hasPermission(): Boolean {
        return EasyPermissions.hasPermissions(getApplication(), Manifest.permission.WRITE_CALENDAR)
    }

    /**
     * delete the event based on the id
     */
    fun deleteEvent(id: String) {
        if (hasPermission()) {
            val cr: ContentResolver = getApplication<MyApp>().contentResolver
            val uri = CalendarContract.Events.CONTENT_URI

            cr.delete(uri, CalendarContract.Events._ID + " = ?", arrayOf(id))
        }
    }

    override fun onCleared() {
        // stop listening the broadcast updates
        LocalBroadcastManager.getInstance(getApplication()).unregisterReceiver(receiver)
        super.onCleared()
    }
}