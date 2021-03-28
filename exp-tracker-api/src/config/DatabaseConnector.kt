package com.exp.tracker.config

import com.exp.tracker.repository.CategoriesTable
import com.exp.tracker.repository.GroupsTable
import com.exp.tracker.repository.TransactionsTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseConnector {

    fun init(databaseConfigurationProperties: DatabaseConfigurationProperties) {
        Database.connect(hikari(databaseConfigurationProperties))
        transaction {
            SchemaUtils.create(GroupsTable)
            SchemaUtils.create(CategoriesTable)
            SchemaUtils.create(TransactionsTable)
        }
    }

    private fun hikari(databaseConfigurationProperties: DatabaseConfigurationProperties): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = databaseConfigurationProperties.className
            jdbcUrl = databaseConfigurationProperties.jdbcUrl
            maximumPoolSize = databaseConfigurationProperties.maxPoolSize
            isAutoCommit = databaseConfigurationProperties.autoCommit
            transactionIsolation = databaseConfigurationProperties.transactionIsolation
            username = databaseConfigurationProperties.username
            password = databaseConfigurationProperties.password
        }
        config.validate()
        return HikariDataSource(config)
    }
}