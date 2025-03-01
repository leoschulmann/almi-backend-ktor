package com.leoschulmann.almi.tables

import com.leoschulmann.almi.enums.GrammaticalGender
import com.leoschulmann.almi.enums.GrammaticalPerson
import com.leoschulmann.almi.enums.Plurality
import com.leoschulmann.almi.enums.Tense
import org.jetbrains.exposed.dao.id.LongIdTable

object VerbFormTable : LongIdTable("verbform") {
    val verb = reference("verb_id", VerbTable)
    val value = varchar("value", 255)
    val tense = enumerationByName("tense", 32, Tense::class)
    val person = enumerationByName("person", 32, GrammaticalPerson::class)
    val plurality = enumerationByName("plurality", 32, Plurality::class)
    val gender = enumerationByName("gender", 32, GrammaticalGender::class)
    val version = integer("version").default(0)
}