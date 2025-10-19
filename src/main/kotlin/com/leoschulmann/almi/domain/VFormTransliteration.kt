package com.leoschulmann.almi.domain

import com.leoschulmann.almi.enums.Lang
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable


object VerbFormTranslitTable : LongIdTable("appdata.verbformtranslit") {
    val verbForm = reference("verbform_id", VerbFormTable)
    val value = varchar("value", 255)
    val lang = enumerationByName("lang", 16, Lang::class)
    val version = integer("version").default(0)
}

class VerbFormTransliteration(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<VerbFormTransliteration>(VerbFormTranslitTable)

    var verbForm: VerbForm by VerbForm referencedOn VerbFormTranslitTable.verbForm
    var value: String by VerbFormTranslitTable.value
    var version: Int by VerbFormTranslitTable.version
    var lang: Lang by VerbFormTranslitTable.lang

    fun toDto() = TransliterationDto(id.value, value, version, lang)
}

@Serializable
data class TransliterationDto(
    val id: Long,
    @SerialName("v")
    val value: String,
    @SerialName("ver")
    val version: Int,
    val lang: Lang
)


@Serializable
data class UpsertVFormTransliterationDto(
    val id: Long?,
    @SerialName("v") val value: String,
    val lang: Lang
)