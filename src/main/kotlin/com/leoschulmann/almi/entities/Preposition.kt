package com.leoschulmann.almi.entities

import com.leoschulmann.almi.tables.PrepositionTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Preposition(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Preposition>(PrepositionTable)
    var value by PrepositionTable.value
    var version by PrepositionTable.version
}