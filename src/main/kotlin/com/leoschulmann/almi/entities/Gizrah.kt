package com.leoschulmann.almi.entities

import com.leoschulmann.almi.tables.GizrahTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Gizrah(id: EntityID<Long>) : LongEntity(id) {
    companion object  : LongEntityClass<Gizrah>(GizrahTable)
    var value by GizrahTable.value
    var version by GizrahTable.version
}