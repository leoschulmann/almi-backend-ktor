package com.leoschulmann.almi.tables

import com.leoschulmann.almi.enums.Lang
import org.jetbrains.exposed.dao.id.LongIdTable

object VerbTranslationTable : LongIdTable("appdata.verbtranslate") {
    val value = varchar("value", 64)
    val lang = enumerationByName("lang", 16, Lang::class)
    val verb = reference("verb_id", VerbTable)
    val version = integer("version").default(0)
}
