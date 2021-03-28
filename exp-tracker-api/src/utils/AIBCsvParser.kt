package com.exp.tracker.utils

import com.exp.tracker.models.Transaction
import com.exp.tracker.models.Type
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AIBCsvParser : CsvParser {
    override fun parse(rows: List<Map<String, String>>): List<Transaction> {
        return rows.map {
            val type = if (it["Transaction Type"] != "Credit") Type.DEBIT else Type.CREDIT
            Transaction(
                id = 1L,
                description = it.getOrDefault(" Description1", "N/A"),
                date = LocalDate.parse(
                    it[" Posted Transactions Date"],
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                ),
                type = type,
                amount = when (type) {
                    Type.DEBIT -> BigDecimal(it[" Debit Amount"]?.replace(",", ""))
                    Type.CREDIT -> BigDecimal(it[" Credit Amount"]?.replace(",", ""))
                }
            )
        }
    }
}