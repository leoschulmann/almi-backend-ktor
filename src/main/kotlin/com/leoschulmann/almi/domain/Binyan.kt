package com.leoschulmann.almi.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object BinyanTable : LongIdTable("appdata.binyan") {
    val value = varchar("value", 32).uniqueIndex()
    val version = integer("version").default(0)
}

class Binyan(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Binyan>(BinyanTable)

    var value by BinyanTable.value
    var version by BinyanTable.version
    
    fun toDto() = BinyanDto(id.value, value, version)
}

@Serializable
data class BinyanDto(val id: Long, @SerialName("b") val value: String, @SerialName("ver") val version: Int)

@Serializable
data class ReqBinyanDto(val id: Long, @SerialName("b") val value: String)