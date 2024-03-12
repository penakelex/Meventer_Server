package org.penakelex.routes.event

enum class EventsType(val type: String) {
    All(""),
    Participant("participant"),
    Organizer("organizer"),
    Featured("featured"),
    Originator("originator")
}