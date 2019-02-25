package com.remindersample.ui.eventDetails

import android.os.Bundle
import com.remindersample.BaseActivity
import com.remindersample.R
import com.remindersample.databinding.ActivityEventBinding

class Event : BaseActivity<ActivityEventBinding, EventViewModel>() {
    companion object {
        const val EVENT_ID = "event_id"
    }

    val EVENT_MINS = arrayOf(30, 20, 10, 5)

    override fun getLayoutId(): Int {
        return R.layout.activity_event
    }

    override fun getViewModelClass(): Class<EventViewModel> {
        return EventViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = intent?.extras
        val eventId = bundle?.getString(EVENT_ID, "")

        if (eventId.isNullOrEmpty()) {
            throw Exception("Event Id must not be empty")
        }

        viewModel.init(eventId)

        binding.addReminder.setOnClickListener {
            val reminders = viewModel.reminders.value
            if (reminders != null) {
                for (i: Int in EVENT_MINS) {
                    if (!reminders.toString().contains("${i}")) {
                        viewModel.addReminder(i)
                        break
                    }
                }
            }
        }
    }
}
