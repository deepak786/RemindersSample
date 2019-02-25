package com.remindersample.models

class Event {
    var title = ""
    var desc = ""
    var id = ""
    var time = ""

    override fun toString(): String {
        return "(${title} ---- ${desc} ---- ${id}})"
    }
}