package com.leoschulmann.almi

import com.leoschulmann.almi.domain.*
import com.leoschulmann.almi.dbhelper.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Schema
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.sql.DriverManager

private val log = LoggerFactory.getLogger("DB")

fun Application.configureDatabase() {
    val url = environment.config.property("postgres.url").getString()
    val user = environment.config.property("postgres.user").getString()
    val password = environment.config.property("postgres.password").getString()

    runMigrations(url, user, password)

    Database.connect(hikari(url, user, password))

    validateSchema(
        BinyanTable, GizrahTable, PrepositionTable, RootTable,
        VerbTable, VerbGizrahJointable, VerbPrepositionJointable,
        VerbTranslationTable, VerbFormTable, VerbFormTranslitTable,
        VerbFormExampleTable, VerbFormExampleTranslationTable
    )
}

private fun runMigrations(url: String, user: String, password: String) {
    log.info("Running Liquibase migrations...")
    val connection = DriverManager.getConnection(url, user, password)
    val database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(connection))
    Liquibase("db/changelog/db.changelog-master.yaml", ClassLoaderResourceAccessor(), database).use { lb ->
        lb.update("")
    }
    log.info("Liquibase migrations complete")
}

private fun validateSchema(vararg tables: Table) {
    transaction {
        SchemaUtils.setSchema(Schema("appdata"))

        val existingTables = exec(
            "SELECT tablename FROM pg_tables WHERE schemaname = 'appdata'"
        ) { rs ->
            val names = mutableListOf<String>()
            while (rs.next()) names.add(rs.getString(1))
            names
        } ?: emptyList()

        val missingTables = tables.filter { table ->
            val shortName = table.tableName.substringAfterLast(".")
            shortName !in existingTables
        }.map { it.tableName }

        val missingColumns = SchemaUtils.addMissingColumnsStatements(*tables)

        val issues = missingTables.map { "Missing table: $it" } + missingColumns
        if (issues.isNotEmpty()) {
            issues.forEach { log.error("Schema validation: $it") }
            error("Exposed model does not match the database schema — check migrations")
        }
        log.info("Schema validation OK")
    }
}

private fun hikari(url: String, user: String, password: String): HikariDataSource {
    val config = HikariConfig().apply {
        driverClassName = "org.postgresql.Driver"
        jdbcUrl = url
        username = user
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    }
    config.password = password
    config.validate()
    return HikariDataSource(config)
}
