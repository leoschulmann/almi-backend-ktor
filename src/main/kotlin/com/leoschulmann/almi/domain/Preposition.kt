package com.leoschulmann.almi.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column


object PrepositionTable : LongIdTable("appdata.preposition") {
    val value: Column<String> = varchar("value", 16).uniqueIndex()
    val version: Column<Int> = integer("version").default(0)
}


class Preposition(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Preposition>(PrepositionTable)
    var value by PrepositionTable.value
    var version by PrepositionTable.version

    fun toDto() = PrepositionDto(id.value, value, version)
}


@Serializable
data class PrepositionDto(val id: Long, @SerialName("p") val value: String, @SerialName("ver") val version: Int)


