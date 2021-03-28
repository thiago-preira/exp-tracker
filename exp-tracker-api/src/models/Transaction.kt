package com.exp.tracker.models

import com.exp.tracker.utils.BigDecimalSerializer
import com.exp.tracker.utils.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDate

@Serializable
data class Transaction(
    val id: Long,
    val description: String,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
    val type: Type,
    @Serializable(with = BigDecimalSerializer::class)
    val amount: BigDecimal,
    val category: Category? = null
)

enum class Type {
    CREDIT,
    DEBIT
}
