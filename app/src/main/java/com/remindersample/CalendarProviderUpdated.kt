package com.remindersample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager

class CalendarProviderUpdated : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        println(">>>>Event received")
        if (context != null) {
            val intentAction = Intent(Constants.MY_BROADCAST)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intentAction)
        }
    }

}