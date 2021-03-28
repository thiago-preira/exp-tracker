package com.exp.tracker.services

import com.exp.tracker.models.Transaction
import com.exp.tracker.repository.TransactionsRepository
import com.exp.tracker.routes.TransactionRequest
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDate

class TransactionsService(private val transactionsRepository: TransactionsRepository) {


    suspend fun save(transactions: List<Transaction>) = newSuspendedTransaction {
        transactionsRepository.save(transactions)
    }

    suspend fun findAllByDate(begin: LocalDate, end: LocalDate): List<Transaction> = newSuspendedTransaction {
        transactionsRepository.findByDates(begin, end)
    }

    suspend fun update(id: Long, transactionRequest: TransactionRequest) = newSuspendedTransaction {
        transactionsRepository.update(id, transactionRequest)
    }

    suspend fun delete(begin: LocalDate, end: LocalDate) = newSuspendedTransaction {
        transactionsRepository.delete(begin, end)
    }

}