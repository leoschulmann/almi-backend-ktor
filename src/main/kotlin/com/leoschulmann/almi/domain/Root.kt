package com.leoschulmann.almi.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable


object RootTable : LongIdTable("appdata.root") {
    val value = varchar("value", 16).uniqueIndex()
    val version = integer("version").default(0)
}

class Root(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Root>(RootTable)

    var value by RootTable.value
    var version by RootTable.version

    fun toDto() = RootDto(id.value, value, version)
}

@Serializable
data class RootDto(val id: Long, @SerialName("r") val value: String, @SerialName("ver") val version: Int)

@Serializable
data class UpdateRootDto(val id: Long, @SerialName("r") val value: String)