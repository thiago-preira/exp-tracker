package com.exp.tracker.services

import com.exp.tracker.models.Category
import com.exp.tracker.repository.CategoriesRepository
import com.exp.tracker.routes.CategoriesRequest
import io.ktor.features.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class CategoryService(private val categoriesRepository: CategoriesRepository) {

    suspend fun findById(id: Long): Category = newSuspendedTransaction {
        categoriesRepository.findById(id) ?: throw NotFoundException("No category found with id $id")
    }

    suspend fun findAll(): List<Category> = newSuspendedTransaction {
        categoriesRepository.findAll()
    }

    suspend fun save(categoriesRequest: CategoriesRequest): Category = newSuspendedTransaction {
        categoriesRepository.store(categoriesRequest)
    }

    suspend fun update(id: Long, categoriesRequest: CategoriesRequest) = newSuspendedTransaction {
        categoriesRepository.findById(id)
        categoriesRepository.update(id, categoriesRequest)
    }

}