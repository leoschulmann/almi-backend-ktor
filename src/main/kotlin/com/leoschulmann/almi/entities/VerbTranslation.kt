package com.leoschulmann.almi.entities

import com.leoschulmann.almi.tables.VerbTranslationTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class VerbTranslation(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<VerbTranslation>(VerbTranslationTable)
    
    var value by VerbTranslationTable.value
    var version by VerbTranslationTable.version
    var verb by Verb referencedOn VerbTranslationTable.verb
    var lang by VerbTranslationTable.lang
}
