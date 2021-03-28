package com.exp.tracker.repository

import com.exp.tracker.models.Category
import com.exp.tracker.models.Group
import com.exp.tracker.routes.CategoriesRequest
import io.ktor.features.*
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

internal object CategoriesTable : LongIdTable(name = "categories") {
    val name: Column<String> = varchar("name", 100)
    val groupId = reference("group_id", GroupsTable.id)

    fun toDomain(row: ResultRow): Category {
        return Category(
            id = row[id].value,
            name = row[name],
            group = Group(
                id = row[groupId].value,
                description = row[GroupsTable.description]
            )
        )
    }
}

class CategoriesRepository {
    fun findById(id: Long): Category? {
        return transaction {
            (CategoriesTable innerJoin GroupsTable).select {
                CategoriesTable.id eq id
            }.map { CategoriesTable.toDomain(it) }
                .firstOrNull()
        }
    }

    fun findAll(): List<Category> {
        return transaction {
            (CategoriesTable innerJoin GroupsTable).selectAll().map { CategoriesTable.toDomain(it) }
        }
    }

    fun store(categoriesRequest: CategoriesRequest): Category {
        return transaction {
            val id = CategoriesTable.insertAndGetId {
                it[name] = categoriesRequest.name
                it[groupId] = categoriesRequest.group.id
            }.value
            Category(
                id,
                categoriesRequest.name,
                categoriesRequest.group
            )
        }
    }

    fun update(id: Long, categoriesRequest: CategoriesRequest) {
        return transaction {
            val updated = CategoriesTable.update({ CategoriesTable.id eq id }) {
                it[name] = categoriesRequest.name
                it[groupId] = categoriesRequest.group.id
            }
            if (updated == 0) throw NotFoundException("No category found with id $id")
        }
    }

}