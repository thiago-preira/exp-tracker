package com.exp.tracker.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.healthRouting() {
    route("/health") {
        get {
            call.respond(HttpStatusCode.OK, "Ok")
        }
    }
}