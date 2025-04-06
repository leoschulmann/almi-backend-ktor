package com.leoschulmann.almi.tables

import com.leoschulmann.almi.enums.Lang
import org.jetbrains.exposed.dao.id.LongIdTable

object VerbFormTranslitTable: LongIdTable("appdata.verbformtranslit") {
    val verbForm = reference("verbform_id", VerbFormTable)
    val value = varchar("value", 255)
    val lang = enumerationByName("lang", 16, Lang::class)
    val version = integer("version").default(0)
}