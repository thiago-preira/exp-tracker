package com.exp.tracker.providers

import java.io.File

interface CSVProvider {
    fun read(file: File): List<Map<String, String>>
    fun export(rows: List<List<String>>): File
}