package com.leoschulmann.almi.domain

import com.leoschulmann.almi.enums.Lang
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable


object VerbTranslationTable : LongIdTable("appdata.verbtranslate") {
    val value = varchar("value", 64)
    val lang = enumerationByName("lang", 16, Lang::class)
    val verb = reference("verb_id", VerbTable)
    val version = integer("version").default(0)
}

class VerbTranslation(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<VerbTranslation>(VerbTranslationTable)

    var value by VerbTranslationTable.value
    var version by VerbTranslationTable.version
    var verb by Verb referencedOn VerbTranslationTable.verb
    var lang by VerbTranslationTable.lang

    fun toDto() = VerbTranslationDto(id.value, value, version, lang)
}

@Serializable
data class VerbTranslationDto(
    val id: Long,
    
    @SerialName("t") val value: String,
    
    @SerialName("ver") val version: Int,
    
    @SerialName("l") val lang: Lang
)

@Serializable
data class CreateVerbTranslationDto(
    @SerialName("t") val value: String,
    
    @SerialName("l") val lang: Lang
)
