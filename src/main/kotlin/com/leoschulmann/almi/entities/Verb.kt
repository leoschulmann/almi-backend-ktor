package com.leoschulmann.almi.entities

import com.leoschulmann.almi.tables.VerbGizrahJointable
import com.leoschulmann.almi.tables.VerbPrepositionJointable
import com.leoschulmann.almi.tables.VerbTable
import com.leoschulmann.almi.tables.VerbTranslationTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable

class Verb(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Verb>(VerbTable)

    var value by VerbTable.value
    var version by VerbTable.version
    var binyan by Binyan referencedOn VerbTable.binyan
    var root by Root referencedOn VerbTable.root

    var prepositions: SizedIterable<Preposition> by Preposition via VerbPrepositionJointable
    var gizrahs: SizedIterable<Gizrah> by Gizrah via VerbGizrahJointable
    val translations: SizedIterable<VerbTranslation> by VerbTranslation referrersOn VerbTranslationTable.verb
}