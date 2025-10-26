package com.leoschulmann.almi.domain

import com.leoschulmann.almi.enums.Lang
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.SizedIterable

object VerbFormExampleTable : LongIdTable("appdata.verbformexample") {
    val verbForm = reference("verbform_id", VerbFormTable)
    val value = varchar("value", 255)
    val file = varchar("file_id", 255).nullable()
    val version = integer("version").default(0)
}

class VerbFormExample(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<VerbFormExample>(VerbFormExampleTable)

    var verbForm: VerbForm by VerbForm referencedOn VerbFormExampleTable.verbForm
    var value by VerbFormExampleTable.value
    var file: String? by VerbFormExampleTable.file
    var version by VerbFormExampleTable.version
    val translations: SizedIterable<VerbFormExampleTranslation> by VerbFormExampleTranslation referrersOn VerbFormExampleTranslationTable.example

    fun toDto() = VerbFormExampleDto(
        id.value,
        value,
        version,
        file,
        translations.map { it.toDto() }
    )
}

@Serializable
data class VerbFormExampleDto(
    val id: Long,
    @SerialName("e")
    val value: String,
    @SerialName("ver")
    val version: Int,
    @SerialName("f")
    val file: String?,
    @SerialName("t")
    val translations: List<VFormExampleTr8nDto>
)

@Serializable
data class CreateVerbFormExampleDto(
    @SerialName("f_id") val verbFormId: Long,
    @SerialName("e") val value: String,
    @SerialName("f") val file: String?,
    @SerialName("tr") val translations: List<Pair<Lang, String>>
)

@Serializable
data class UpdateVerbFormExampleDto(
    val id: Long,
    @SerialName("e") val value: String,
    @SerialName("f") val file: String?,
    @SerialName("tr") val translations: List<UpsertVFormExampleTr8nDto>
)
