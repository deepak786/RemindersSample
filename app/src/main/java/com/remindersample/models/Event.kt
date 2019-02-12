package com.remindersample.models

class Event {
    var title = ""
    var desc = ""
    var id = ""

    override fun toString(): String {
        return "(${title} ---- ${desc} ---- ${id}})"
    }
}