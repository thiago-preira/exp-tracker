package com.exp.tracker.config

data class DatabaseConfigurationProperties(
    val className: String,
    val jdbcUrl: String,
    val maxPoolSize: Int,
    val autoCommit: Boolean,
    val transactionIsolation: String,
    val username: String,
    val password: String
)