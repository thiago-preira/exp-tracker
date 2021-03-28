package com.exp.tracker.services

import com.exp.tracker.models.Group
import com.exp.tracker.repository.GroupsRepository
import com.exp.tracker.routes.GroupRequest
import io.ktor.features.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class GroupService(private val groupsRepository: GroupsRepository) {
    suspend fun findById(id: Long): Group = newSuspendedTransaction {
        groupsRepository.findById(id) ?: throw NotFoundException("No group found with id $id")
    }

    suspend fun findAll(): List<Group> = newSuspendedTransaction {
        groupsRepository.findAll()
    }

    suspend fun save(groupRequest: GroupRequest): Group = newSuspendedTransaction {
        groupsRepository.store(groupRequest)
    }

    suspend fun remove(id: Long) = newSuspendedTransaction {
        groupsRepository.delete(id)
    }
}