package com.remindersample.models

class Reminder {
    var minutes = ""
    var method = ""
    var id = ""

    override fun toString(): String {
        return "(${minutes} ---- ${method} ---- ${id}})"
    }
}