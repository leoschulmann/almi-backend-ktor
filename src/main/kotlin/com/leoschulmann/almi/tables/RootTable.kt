package com.leoschulmann.almi.tables

import org.jetbrains.exposed.dao.id.LongIdTable

object RootTable : LongIdTable("appdata.root") {
    val value = varchar("value", 16).uniqueIndex()
    val version = integer("version").default(0)
}