package com.leoschulmann.almi.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

object GizrahTable : LongIdTable("appdata.gizrah") {
    val value: Column<String> = varchar("value", 32).uniqueIndex()
    val version: Column<Int> = integer("version").default(0)
}

class Gizrah(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Gizrah>(GizrahTable)

    var value by GizrahTable.value
    var version by GizrahTable.version

    fun toDto() = GizrahDto(id.value, value, version)
}

@Serializable
data class GizrahDto(val id: Long, @SerialName("g") val value: String, @SerialName("ver") val version: Int)

@Serializable
data class ReqGizrahDto(val id: Long, @SerialName("g") val value: String)