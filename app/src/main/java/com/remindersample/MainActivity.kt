package com.remindersample

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.provider.CalendarContract
import android.support.v4.app.ActivityCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.remindersample.adapter.EventsAdapter
import com.remindersample.databinding.ActivityMainBinding
import com.remindersample.models.Event
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, EventsAdapter.EventsCallback {
    private val CODE: Int = 123
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: EventsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.newEvent.setOnClickListener {
            startActivity(Intent(this@MainActivity, NewEvent::class.java))
        }

        binding.events.layoutManager = LinearLayoutManager(this)
        adapter = EventsAdapter(ArrayList(), this)
        binding.events.adapter = adapter

        val perm: String = Manifest.permission.WRITE_CALENDAR
        if (EasyPermissions.hasPermissions(this, perm)) {
            // Already have permission, do the thing
            // ...
            getEventsList()
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this, "Please allow the permissions",
                CODE, perm
            )
        }
    }

    override fun onResume() {
        super.onResume()
        // TODO add this event listener in view model
        val filter = IntentFilter()
        filter.addAction(Constants.MY_BROADCAST)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        when (requestCode) {
            CODE -> {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                getEventsList()
            }
        }
    }

    /**
     * function to get the list of reminders
     */
    private fun getEventsList() {
        println(">>>>Getting the list of Events")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        )
            return

        val list: ArrayList<Event> = ArrayList()
        val cur: Cursor?
        val cr = contentResolver

        val mProjection = arrayOf(
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events._ID
        )

        val uri = CalendarContract.Events.CONTENT_URI
        val selection =
            CalendarContract.Events.CUSTOM_APP_PACKAGE + " = ? AND " + CalendarContract.Events.DELETED + " = ? "
        val selectionArgs = arrayOf(packageName, "0")

        cur = cr.query(uri, mProjection, selection, selectionArgs, null)

        while (cur!!.moveToNext()) {
            val event = Event()

            event.title = cur.getString(cur.getColumnIndex(CalendarContract.Events.TITLE))
            event.desc = cur.getString(cur.getColumnIndex(CalendarContract.Events.DESCRIPTION))
            event.id = cur.getString(cur.getColumnIndex(CalendarContract.Events._ID))

            list.add(event)
        }
        cur.close()

        adapter.updateList(list)
    }

    /**
     * receiver to listen for new calendar events
     */
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            getEventsList()
        }
    }

    override fun deleteEvent(id: String) {
        // TODO delete the event
    }

    override fun viewEvent(id: String) {
        // TODO new screen to view the event details
    }
}
