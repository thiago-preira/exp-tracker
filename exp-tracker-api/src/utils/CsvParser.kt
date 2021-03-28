package com.exp.tracker.utils

import com.exp.tracker.models.Transaction

interface CsvParser {
    fun parse(rows: List<Map<String, String>>): List<Transaction>
}