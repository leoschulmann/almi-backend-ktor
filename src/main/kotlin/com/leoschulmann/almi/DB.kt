package com.leoschulmann.almi

import com.leoschulmann.almi.tables.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Schema
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    DatabaseFactory.init() 
    // Additional Ktor module configurations will go here
}


object DatabaseFactory {
    fun init() {
        Database.connect(hikari())
        transaction {
            SchemaUtils.setSchema(Schema("appdata"))
            SchemaUtils.create(
                BinyanTable,
                GizrahTable,
                PrepositionTable,
                RootTable,
                VerbGizrahJointable,
                VerbPrepositionJointable,
                VerbTable, 
                VerbTranslationTable,
                VerbFormTable,
                VerbFormTranslitTable
            )
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = "jdbc:postgresql://localhost:33582/almi3"
            username = "postgres"
            password = "postgres"
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }
        config.validate()
        return HikariDataSource(config)
    }
}
