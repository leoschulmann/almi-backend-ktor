package com.leoschulmann.almi.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

object GizrahTable : LongIdTable("appdata.gizrah") {
    val value: Column<String> = varchar("value", 32).uniqueIndex()
    val version: Column<Int> = integer("version").default(0)
}