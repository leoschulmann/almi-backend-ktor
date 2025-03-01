package com.leoschulmann.almi.entities

import com.leoschulmann.almi.enums.GrammaticalGender
import com.leoschulmann.almi.enums.GrammaticalPerson
import com.leoschulmann.almi.enums.Plurality
import com.leoschulmann.almi.enums.Tense
import com.leoschulmann.almi.tables.VerbFormTable
import com.leoschulmann.almi.tables.VerbFormTranslitTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable

class VerbForm(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<VerbForm>(VerbFormTable)

    var verb: Verb by Verb referencedOn VerbFormTable.verb
    var value: String by VerbFormTable.value
    var version: Int by VerbFormTable.version
    var tense: Tense by VerbFormTable.tense
    var person: GrammaticalPerson by VerbFormTable.person
    var plurality: Plurality by VerbFormTable.plurality
    var gender: GrammaticalGender by VerbFormTable.gender
    val transliterations: SizedIterable<VerbFormTransliteration> by VerbFormTransliteration referrersOn VerbFormTranslitTable.verbForm
}