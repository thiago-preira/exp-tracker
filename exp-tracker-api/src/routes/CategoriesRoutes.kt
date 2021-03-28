package com.exp.tracker.routes

import com.exp.tracker.ext.extractValueAsLong
import com.exp.tracker.models.Group
import com.exp.tracker.services.CategoryService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class CategoriesRequest(val name: String, val group: Group)

fun Route.categoriesRouting(categoryService: CategoryService) {
    route("categories") {
        get("{id}") {
            val id = call.parameters.extractValueAsLong("id")
            val category = categoryService.findById(id)
            call.respond(category)
        }
        get {
            call.respond(categoryService.findAll())
        }
        post {
            val categoriesRequest = call.receive<CategoriesRequest>()
            val saved = categoryService.save(categoriesRequest)
            call.respond(HttpStatusCode.Created, saved)
        }
        put("{id}") {
            val id = call.parameters.extractValueAsLong("id")
            val categoriesRequest = call.receive<CategoriesRequest>()
            categoryService.update(id, categoriesRequest)
            call.respond(HttpStatusCode.OK)
        }
    }
}