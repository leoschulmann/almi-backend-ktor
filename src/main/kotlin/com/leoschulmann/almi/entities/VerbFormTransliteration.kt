package com.leoschulmann.almi.entities

import com.leoschulmann.almi.enums.Lang
import com.leoschulmann.almi.tables.VerbFormTranslitTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class VerbFormTransliteration(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<VerbFormTransliteration>(VerbFormTranslitTable)

    var verbForm: VerbForm by VerbForm referencedOn VerbFormTranslitTable.verbForm
    var value: String by VerbFormTranslitTable.value
    var version: Int by VerbFormTranslitTable.version
    var lang: Lang by VerbFormTranslitTable.lang
}