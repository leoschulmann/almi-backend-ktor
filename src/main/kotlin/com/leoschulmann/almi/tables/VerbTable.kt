package com.leoschulmann.almi.tables

import org.jetbrains.exposed.dao.id.LongIdTable

object VerbTable : LongIdTable("appdata.verb") {
    val value = varchar("value", 255).uniqueIndex()
    val version = integer("version").default(0)
    val root = reference("root_id", RootTable)
    val binyan = reference("binyan_id", BinyanTable)
}