package com.leoschulmann.almi.entities

import com.leoschulmann.almi.tables.RootTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Root(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Root>(RootTable)
    var value by RootTable.value
    var version by RootTable.version
}