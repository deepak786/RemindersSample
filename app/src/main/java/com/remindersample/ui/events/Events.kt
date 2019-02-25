package com.remindersample.ui.events

import android.Manifest
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.remindersample.BaseActivity
import com.remindersample.R
import com.remindersample.adapter.EventsAdapter
import com.remindersample.databinding.ActivityMainBinding
import com.remindersample.ui.eventDetails.Event
import com.remindersample.ui.newEvent.NewEvent
import pub.devrel.easypermissions.EasyPermissions


class Events : BaseActivity<ActivityMainBinding, EventsViewModel>(), EasyPermissions.PermissionCallbacks,
    EventsAdapter.EventsCallback {

    private val CODE: Int = 123
    private lateinit var adapter: EventsAdapter

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun getViewModelClass(): Class<EventsViewModel> {
        return EventsViewModel::class.java
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.init()

        binding.newEvent.setOnClickListener {
            startActivity(Intent(this@Events, NewEvent::class.java))
        }

        binding.events.layoutManager = LinearLayoutManager(this)
        adapter = EventsAdapter(ArrayList(), this)
        binding.events.adapter = adapter

        val perm: String = Manifest.permission.WRITE_CALENDAR
        if (EasyPermissions.hasPermissions(this, perm)) {
            viewModel.getEvents()
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this, "Please allow the permissions",
                CODE, perm
            )
        }

        viewModel.events.observe(this, Observer {
            adapter.updateList(it!!)
        })
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
                viewModel.getEvents()
            }
        }
    }


    override fun deleteEvent(id: String) {
        viewModel.deleteEvent(id)
    }

    override fun viewEvent(id: String) {
        val intent = Intent(this, Event::class.java)
        intent.putExtra(Event.EVENT_ID, id)
        startActivity(intent)
    }
}
