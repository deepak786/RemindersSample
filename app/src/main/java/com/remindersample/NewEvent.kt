package com.remindersample

import android.Manifest
import android.content.ContentValues
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Toast
import com.remindersample.databinding.ActivityNewEventBinding
import pub.devrel.easypermissions.EasyPermissions
import java.util.*


class NewEvent : AppCompatActivity() {
    private lateinit var binding: ActivityNewEventBinding
    private var startTime: Calendar = Calendar.getInstance()
    private var endTime: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_event)

        // add 1 hours
        startTime.add(Calendar.MINUTE, 60)
        binding.startTime.setText(getFormattedDate(startTime))

        // add 2 hours for end time
        endTime.add(Calendar.MINUTE, 120)
        binding.endTime.setText(getFormattedDate(endTime))

        binding.save.setOnClickListener {
            val title: String = binding.title.text.toString().trim()
            val desc: String = binding.description.text.toString().trim()

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(this, "Please provide title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_CALENDAR)) {
                // Already have permission, do the thing
                insertEvent(title, desc)
            } else {
                Toast.makeText(this, "Please enable the permissions", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * get the formatted date
     */
    private fun getFormattedDate(calendar: Calendar): String {
        return String.format(
            Locale.getDefault(),
            "%02d/%02d/%d %02d:%02d",
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR),
            calendar.get(
                Calendar.HOUR_OF_DAY
            ),
            calendar.get(Calendar.MINUTE)
        )
    }

    /**
     * insert this new event
     */
    private fun insertEvent(title: String, desc: String) {
        val eventsUri: Uri = CalendarContract.Events.CONTENT_URI
        val eventValues = ContentValues()

        eventValues.put(
            CalendarContract.Events.CALENDAR_ID,
            1
        ) // id, We need to choose from our mobile, for primary its 1
        eventValues.put(CalendarContract.Events.TITLE, title) // title of the event
        eventValues.put(CalendarContract.Events.DESCRIPTION, desc) // description of the event
        eventValues.put(CalendarContract.Events.DTSTART, startTime.timeInMillis)
        eventValues.put(CalendarContract.Events.DTEND, endTime.timeInMillis)
        eventValues.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().displayName)
        eventValues.put(CalendarContract.Events.CUSTOM_APP_PACKAGE, packageName)
        eventValues.put(CalendarContract.Events.CUSTOM_APP_URI, Constants.MY_URI + System.currentTimeMillis())
        // values.put("allDay", 1); //If it is bithday alarm or such kind (which should remind me for whole day) 0 for false, 1 for true

        val eventUri = contentResolver.insert(eventsUri, eventValues)
        if (eventUri != null) {
            val eventID = eventUri.lastPathSegment
            if (binding.reminder.isChecked) {
                // add reminder

                val remindersUri: Uri = CalendarContract.Reminders.CONTENT_URI

                val reminderValues = ContentValues()

                reminderValues.put(CalendarContract.Reminders.EVENT_ID, eventID)
                reminderValues.put(
                    CalendarContract.Reminders.MINUTES,
                    30
                ) // Testing with 30 mins
                reminderValues.put(
                    CalendarContract.Reminders.METHOD,
                    1
                ) // Alert Methods: Default(0), Alert(1), Email(2), SMS(3)

                contentResolver.insert(remindersUri, reminderValues)
            }
            Toast.makeText(this, "Event Added successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
