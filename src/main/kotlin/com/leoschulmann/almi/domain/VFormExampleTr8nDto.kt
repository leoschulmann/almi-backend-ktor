package com.leoschulmann.almi.domain

import com.leoschulmann.almi.enums.Lang
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object VerbFormExampleTranslationTable : LongIdTable("appdata.verbformexampletranslate") {
    val example = reference("example_id", VerbFormExampleTable)
    val lang = enumerationByName(name = "lang", length = 16, klass = Lang::class)
    val value = varchar("value", 255)
    val version = integer("version").default(0)
}

class VerbFormExampleTranslation(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<VerbFormExampleTranslation>(VerbFormExampleTranslationTable)

    var example: VerbFormExample by VerbFormExample referencedOn VerbFormExampleTranslationTable.example
    var lang by VerbFormExampleTranslationTable.lang
    var value by VerbFormExampleTranslationTable.value
    var version by VerbFormExampleTranslationTable.version

    fun toDto() = VFormExampleTr8nDto(id.value, value, lang, version)
}

@Serializable
data class VFormExampleTr8nDto(
    val id: Long,
    @SerialName("t") val value: String,
    @SerialName("l") val lang: Lang,
    @SerialName("ver") val version: Int
)

@Serializable
data class UpsertVFormExampleTr8nDto(
    val id: Long? = null,
    @SerialName("t") val value: String,
    @SerialName("l") val lang: Lang
)
