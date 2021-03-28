package com.exp.tracker.routes

import com.exp.tracker.ext.extractValueAsLocalDate
import com.exp.tracker.ext.extractValueAsLong
import com.exp.tracker.models.Category
import com.exp.tracker.models.Transaction
import com.exp.tracker.models.Type
import com.exp.tracker.providers.CSVProvider
import com.exp.tracker.services.TransactionsService
import com.exp.tracker.utils.BigDecimalSerializer
import com.exp.tracker.utils.CsvParser
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters.firstDayOfMonth
import java.time.temporal.TemporalAdjusters.lastDayOfMonth

@Serializable
data class TransactionRequest(
    val description: String,
    val type: Type,
    val category: Category
)

@Serializable
data class TransactionsSummary(
    @Serializable(with = BigDecimalSerializer::class)
    val total: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val expenses: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val income: BigDecimal
)

fun Route.transactionsRouting(
    csvProvider: CSVProvider,
    transactionsService: TransactionsService,
    csvParser: CsvParser
) {
    route("transactions") {
        post("/upload") {
            // retrieve all multipart data (suspending)
            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                // if part is a file (could be form item)
                if (part is PartData.FileItem) {
                    // retrieve file name of upload
                    val name = part.originalFileName!!
                    val file = File("/tmp/$name")
                    // use InputStream from part to save file
                    part.streamProvider().use { its ->
                        // copy the stream to the file with buffering
                        file.outputStream().buffered().use {
                            // note that this is blocking
                            its.copyTo(it)
                        }
                    }
                    val rows = csvProvider.read(file)
                    val transactions = csvParser.parse(rows)
                    transactionsService.save(transactions)
                }
                // make sure to dispose of the part after use to prevent leaks
                part.dispose()
            }
            call.respond(HttpStatusCode.Accepted)
        }

        get {
            val transactions = getAll(call, transactionsService)
            call.respond(HttpStatusCode.OK, transactions)
        }

        put("{id}") {
            val id = call.parameters.extractValueAsLong("id")
            val transactionRequest = call.receive<TransactionRequest>()
            transactionsService.update(id, transactionRequest)
            call.respond(HttpStatusCode.OK)
        }

        get("total") {
            val transactions = getAll(call, transactionsService)
            val expenses = transactions.filter { it.type == Type.DEBIT }.sumOf { it.amount }
            val income = transactions.filter { it.type == Type.CREDIT }.sumOf { it.amount }
            val total = income.minus(expenses)
            call.respond(
                HttpStatusCode.OK, TransactionsSummary(
                    expenses = expenses,
                    income = income,
                    total = total
                )
            )
        }

        delete {
            val queryParameters: Parameters = call.request.queryParameters
            val begin: LocalDate =
                queryParameters.extractValueAsLocalDate("start", LocalDate.now().with(firstDayOfMonth()))
            val end: LocalDate = queryParameters.extractValueAsLocalDate("end", LocalDate.now().with(lastDayOfMonth()))
            transactionsService.delete(begin, end)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

private suspend fun getAll(call: ApplicationCall, transactionsService: TransactionsService): List<Transaction> {
    val queryParameters: Parameters = call.request.queryParameters
    val begin: LocalDate = queryParameters.extractValueAsLocalDate("start", LocalDate.now().with(firstDayOfMonth()))
    val end: LocalDate = queryParameters.extractValueAsLocalDate("end", LocalDate.now().with(lastDayOfMonth()))
    return transactionsService.findAllByDate(begin, end)
}