package com.exp.tracker.ext

import com.exp.tracker.exceptions.InvalidParameterException
import io.ktor.http.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun Parameters.extractValueAsLong(key: String): Long {
    try {
        val value = this[key] ?: throw InvalidParameterException("No identifier with key $key")
        return value.toLong()
    } catch (exception: Exception) {
        throw InvalidParameterException("Malformed identifier $this")
    }
}

fun Parameters.extractValueAsLocalDate(key: String, defaultValue: LocalDate): LocalDate {
    try {
        val value = this[key] ?: return defaultValue
        return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    } catch (exception: Exception) {
        throw InvalidParameterException("Malformed parameter $this")
    }
}