package org.penakelex.routes.event

enum class EventsType(val type: String) {
    All(""),
    Participant("participant"),
    Organizer("organizer"),
    InFavourites("in favourites"),
    Originator("originator")
}