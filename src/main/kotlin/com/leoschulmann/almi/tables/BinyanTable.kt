package com.leoschulmann.almi.tables

import org.jetbrains.exposed.dao.id.LongIdTable

object BinyanTable : LongIdTable("binyan") {
    val value = varchar("value", 32).uniqueIndex()
    val version = integer("version").default(0)
}