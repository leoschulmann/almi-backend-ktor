package com.leoschulmann.almi.entities

import com.leoschulmann.almi.tables.BinyanTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Binyan(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Binyan>(BinyanTable)

    var value by BinyanTable.value
    var version by BinyanTable.version
}