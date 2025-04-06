package com.leoschulmann.almi.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

object PrepositionTable : LongIdTable("appdata.preposition") {
    val value: Column<String> = varchar("value", 16).uniqueIndex()
    val version: Column<Int> = integer("version").default(0)
}