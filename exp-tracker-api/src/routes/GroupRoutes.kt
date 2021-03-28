package com.exp.tracker.routes

import com.exp.tracker.ext.extractValueAsLong
import com.exp.tracker.services.GroupService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable


@Serializable
data class GroupRequest(val description: String)

fun Route.groupsRouting(groupService: GroupService) {
    route("/groups") {
        get {
            call.respond(groupService.findAll())
        }
        get("{id}") {
            val id = call.parameters.extractValueAsLong("id")
            val group = groupService.findById(id)
            call.respond(group)
        }
        post {
            val groupRequest = call.receive<GroupRequest>()
            val saved = groupService.save(groupRequest)
            call.respond(HttpStatusCode.Created, saved)
        }
        delete("{id}") {
            val id = call.parameters.extractValueAsLong("id")
            groupService.remove(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
