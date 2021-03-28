package com.exp.tracker.repository

import com.exp.tracker.models.Category
import com.exp.tracker.models.Group
import com.exp.tracker.models.Transaction
import com.exp.tracker.models.Type
import com.exp.tracker.repository.TransactionsTable.amount
import com.exp.tracker.repository.TransactionsTable.date
import com.exp.tracker.repository.TransactionsTable.description
import com.exp.tracker.routes.TransactionRequest
import io.ktor.features.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.`java-time`.date
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.LocalDate


internal object TransactionsTable : LongIdTable(name = "transactions") {
    val description: Column<String> = varchar("description", length = 200)
    val date: Column<LocalDate> = date("date")
    val type: Column<Type> = enumerationByName("type", length = 20, Type::class)
    val amount: Column<BigDecimal> = decimal("amount", 10, 2)
    val categoryId = reference("category_id", CategoriesTable.id).nullable()

    fun toDomain(row: ResultRow): Transaction {
        return Transaction(
            id = row[id].value,
            description = row[description],
            date = row[date],
            type = row[type],
            amount = row[amount],
            category = row[categoryId]?.let {
                Category(
                    id = it.value,
                    name = row[CategoriesTable.name],
                    group = Group(
                        id = row[CategoriesTable.groupId].value,
                        description = row[GroupsTable.description]
                    )
                )
            }
        )
    }
}

class TransactionsRepository {
    fun findByDates(begin: LocalDate, end: LocalDate): List<Transaction> {
        return transaction {
            (TransactionsTable leftJoin CategoriesTable leftJoin GroupsTable)
                .select { date.between(begin, end) }.map { TransactionsTable.toDomain(it) }
                .sortedBy { it.date }
        }
    }

    fun save(transactions: List<Transaction>) {
        return transaction {
            TransactionsTable.batchInsert(transactions, shouldReturnGeneratedValues = false) { transaction ->
                this[description] = transaction.description
                this[amount] = transaction.amount
                this[date] = transaction.date
                this[TransactionsTable.type] = transaction.type
            }
        }
    }

    fun update(id: Long, transactionRequest: TransactionRequest) {
        return transaction {
            val updated = TransactionsTable.update({ TransactionsTable.id eq id }) {
                it[description] = transactionRequest.description
                it[categoryId] = EntityID(transactionRequest.category.id, CategoriesTable)
                it[type] = transactionRequest.type
            }
            if (updated == 0) throw NotFoundException("No transaction found with id $id")
        }
    }

    fun delete(begin: LocalDate, end: LocalDate) {
        transaction {
            TransactionsTable.deleteWhere { date.between(begin, end) }
        }
    }
}