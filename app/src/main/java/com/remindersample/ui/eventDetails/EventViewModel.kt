package com.remindersample.ui.eventDetails

import android.Manifest
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import com.remindersample.MyApp
import com.remindersample.models.Event
import com.remindersample.models.Reminder
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

class EventViewModel(application: Application) : AndroidViewModel(application) {
    var event: MutableLiveData<Event> = MutableLiveData()
    var reminders: MutableLiveData<ArrayList<Reminder>> = MutableLiveData()

    lateinit var id: String
    /**
     * listen for broadcast manager to get the events data
     */
    fun init(id: String) {
        this.id = id
        getEventDetails()
        getReminder()
    }

    /**
     * get the list of events
     */
    private fun getEventDetails() {
        if (hasPermission()) {
            val cur: Cursor?
            val cr: ContentResolver = getApplication<MyApp>().contentResolver

            val mProjection = arrayOf(
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events._ID
            )

            val uri = CalendarContract.Events.CONTENT_URI
            val selection =
                CalendarContract.Events._ID + " = ? "
            val selectionArgs = arrayOf(id)

            cur = cr.query(uri, mProjection, selection, selectionArgs, null)

            while (cur!!.moveToNext()) {
                val event = Event()

                event.title = cur.getString(cur.getColumnIndex(CalendarContract.Events.TITLE))
                event.desc = cur.getString(cur.getColumnIndex(CalendarContract.Events.DESCRIPTION))
                event.id = cur.getString(cur.getColumnIndex(CalendarContract.Events._ID))
                event.time = getTime(cur.getLong(cur.getColumnIndex(CalendarContract.Events.DTSTART)))

                this.event.postValue(event)
            }
            cur.close()
        }
    }

    /**
     * get the reminders about the event
     */
    private fun getReminder() {
        if (hasPermission()) {
            val reminders = ArrayList<Reminder>()
            val cur: Cursor?
            val cr: ContentResolver = getApplication<MyApp>().contentResolver

            val mProjection = arrayOf(
                CalendarContract.Reminders.MINUTES,
                CalendarContract.Reminders.METHOD,
                CalendarContract.Reminders._ID
            )

            val uri = CalendarContract.Reminders.CONTENT_URI
            val selection =
                CalendarContract.Reminders.EVENT_ID + " = ? "
            val selectionArgs = arrayOf(id)

            cur = cr.query(uri, mProjection, selection, selectionArgs, null)

            while (cur!!.moveToNext()) {
                val reminder = Reminder()

                reminder.minutes = cur.getString(cur.getColumnIndex(CalendarContract.Reminders.MINUTES))
                reminder.method = cur.getString(cur.getColumnIndex(CalendarContract.Reminders.METHOD))
                reminder.id = cur.getString(cur.getColumnIndex(CalendarContract.Reminders._ID))

                reminders.add(reminder)
            }
            cur.close()
            this.reminders.postValue(reminders)
        }
    }

    fun addReminder(min: Int) {
        val remindersUri: Uri = CalendarContract.Reminders.CONTENT_URI

        val reminderValues = ContentValues()

        reminderValues.put(CalendarContract.Reminders.EVENT_ID, id)
        reminderValues.put(
            CalendarContract.Reminders.MINUTES,
            min
        )
        reminderValues.put(
            CalendarContract.Reminders.METHOD,
            1
        ) // Alert Methods: Default(0), Alert(1), Email(2), SMS(3)
        val cr: ContentResolver = getApplication<MyApp>().contentResolver
        cr.insert(remindersUri, reminderValues)

        // This will not call the broadcast
        // get reminders again
        getReminder()
    }

    /**
     * function to check if we have the permission to perfom the action or not
     */
    private fun hasPermission(): Boolean {
        return EasyPermissions.hasPermissions(getApplication(), Manifest.permission.WRITE_CALENDAR)
    }

    private fun getTime(millis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        return String.format(
            Locale.getDefault(), "%02d/%02d/%d %02d:%02d %s",
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.HOUR),
            calendar.get(Calendar.MINUTE),
            if (calendar.get(Calendar.AM_PM) == Calendar.AM) "am" else "pm"
        )
    }
}