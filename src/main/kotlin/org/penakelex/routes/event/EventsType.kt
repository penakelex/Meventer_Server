package org.penakelex.routes.event

enum class EventsType(val type: String) {
    All(""),
    Participant("participant"),
    Organizer("organizer"),
    Featured("featured")
    //TODO: Add Originator type and request
}