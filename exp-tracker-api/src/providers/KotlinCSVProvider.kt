package com.exp.tracker.providers

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File

class KotlinCSVProvider : CSVProvider {
    override fun read(file: File): List<Map<String, String>> = csvReader().readAllWithHeader(file)

    override fun export(rows: List<List<String>>): File {
        val file = File("/tmp/export-${System.currentTimeMillis()}.csv")
        csvWriter().writeAll(rows, file.absoluteFile)
        return file
    }
}