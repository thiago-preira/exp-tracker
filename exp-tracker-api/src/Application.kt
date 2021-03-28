package com.exp.tracker

import com.exp.tracker.config.DatabaseConfigurationProperties
import com.exp.tracker.config.DatabaseConnector
import com.exp.tracker.exceptions.InvalidParameterException
import com.exp.tracker.providers.KotlinCSVProvider
import com.exp.tracker.repository.CategoriesRepository
import com.exp.tracker.repository.GroupsRepository
import com.exp.tracker.repository.TransactionsRepository
import com.exp.tracker.routes.categoriesRouting
import com.exp.tracker.routes.groupsRouting
import com.exp.tracker.routes.healthRouting
import com.exp.tracker.routes.transactionsRouting
import com.exp.tracker.services.CategoryService
import com.exp.tracker.services.GroupService
import com.exp.tracker.services.TransactionsService
import com.exp.tracker.utils.AIBCsvParser
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    val groupsRepository = GroupsRepository()
    val categoriesRepository = CategoriesRepository()

    val groupService = GroupService(groupsRepository)
    val categoryService = CategoryService(categoriesRepository)

    val csvProvider = KotlinCSVProvider()
    val transactionRepository = TransactionsRepository()
    val transactionService = TransactionsService(transactionRepository)
    val csvParser = AIBCsvParser()

    val databaseConfigProps = DatabaseConfigurationProperties(
        className = environment.config.property("ktor.db.className").getString(),
        jdbcUrl = environment.config.property("ktor.db.jdbcUrl").getString(),
        maxPoolSize = environment.config.property("ktor.db.maxPoolSize").getString().toInt(),
        autoCommit = environment.config.property("ktor.db.autoCommit").getString().toBoolean(),
        transactionIsolation = environment.config.property("ktor.db.transactionIsolation").getString(),
        username = environment.config.property("ktor.db.username").getString(),
        password = environment.config.property("ktor.db.password").getString(),
    )

    DatabaseConnector.init(databaseConfigProps)

    install(ContentNegotiation) {
        json()
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    routing {
        route("/api") {
            groupsRouting(groupService)
            categoriesRouting(categoryService)
            transactionsRouting(csvProvider, transactionService, csvParser)
            healthRouting()
        }

        install(StatusPages) {
            exception<NotFoundException> { cause ->
                call.respond(HttpStatusCode.NotFound, cause.localizedMessage)
            }
            exception<InvalidParameterException> { cause ->
                call.respond(HttpStatusCode.BadRequest, cause.localizedMessage)
            }
        }
    }
}
