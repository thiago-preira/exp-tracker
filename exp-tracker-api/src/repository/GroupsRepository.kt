package com.exp.tracker.repository

import com.exp.tracker.models.Group
import com.exp.tracker.routes.GroupRequest
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

internal object GroupsTable : LongIdTable(name = "groups") {
    val description: Column<String> = varchar("description", 200)
    fun toDomain(row: ResultRow): Group {
        return Group(
            id = row[id].value,
            description = row[description]
        )
    }
}

class GroupsRepository {

    fun findById(id: Long): Group? {
        return transaction {
            GroupsTable.select { GroupsTable.id eq id }
                .map { GroupsTable.toDomain(it) }
                .firstOrNull()
        }
    }

    fun findAll(): List<Group> {
        return GroupsTable.selectAll().map { GroupsTable.toDomain(it) }
    }

    fun store(groupRequest: GroupRequest): Group {
        return transaction {
            val id = GroupsTable.insertAndGetId {
                it[description] = groupRequest.description
            }.value
            Group(
                id,
                groupRequest.description
            )
        }
    }

    fun delete(id: Long) {
        transaction {
            GroupsTable.deleteWhere { GroupsTable.id eq id }
        }
    }

}