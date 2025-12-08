package com.leoschulmann.almi.domain

import com.leoschulmann.almi.dbhelper.VerbGizrahJointable
import com.leoschulmann.almi.dbhelper.VerbPrepositionJointable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.SizedIterable


object VerbTable : LongIdTable("appdata.verb") {
    val value = varchar("value", 255).uniqueIndex()
    val version = integer("version").default(0)
    val root = reference("root_id", RootTable)
    val binyan = reference("binyan_id", BinyanTable)
}


class Verb(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Verb>(VerbTable)

    var value by VerbTable.value
    var version by VerbTable.version
    var binyan by Binyan referencedOn VerbTable.binyan
    var root by Root referencedOn VerbTable.root

    var prepositions: SizedIterable<Preposition> by Preposition via VerbPrepositionJointable
    var gizrahs: SizedIterable<Gizrah> by Gizrah via VerbGizrahJointable
    val translations: SizedIterable<VerbTranslation> by VerbTranslation referrersOn VerbTranslationTable.verb
    val forms: SizedIterable<VerbForm> by VerbForm referrersOn VerbFormTable.verb

    fun toFullDto() = VerbFullDto(
        id.value,
        value,
        version,
        binyan.toDto(),
        root.toDto(),
        gizrahs.map { it.toDto() },
        prepositions.map { it.toDto() },
        translations.map { it.toDto() })

    fun toShortDto() = VerbShortDto(
        id.value, value, version, translations.map { it.toDto() })

    fun toSyncDto() = VerbSyncDto(
        id.value,
        value,
        version,
        root.id.value,
        binyan.id.value,
        gizrahs.map { it.id.value },
        prepositions.map { it.id.value },
        translations.map { it.toDto() })
}

@Serializable
data class VerbFullDto(
    val id: Long,

    @SerialName("v") val value: String,

    @SerialName("ver") val version: Int,

    @SerialName("b") val binyan: BinyanDto,

    @SerialName("r") val root: RootDto,

    @SerialName("g") val gizrahs: List<GizrahDto>,

    @SerialName("p") val prepositions: List<PrepositionDto>,

    @SerialName("t") val translations: List<VerbTranslationDto>
)

@Serializable
data class VerbSyncDto(
    val id: Long,
    
    @SerialName("v") val value: String,
    
    @SerialName("ver") val version: Int,
    
    @SerialName("r_id") val rootId: Long,
    
    @SerialName("b_id") val binyanId: Long,
    
    @SerialName("g_id") val gizrahIds: List<Long>,
    
    @SerialName("p_id") val prepositionIds: List<Long>,
    
    @SerialName("t") val translations: List<VerbTranslationDto>
)

@Serializable
data class VerbShortDto(
    val id: Long,

    @SerialName("v") val value: String,

    @SerialName("ver") val version: Int,

    @SerialName("t") val translations: List<VerbTranslationDto>
)

@Serializable
data class CreateVerbDto(
    @SerialName("v") val value: String,

    @SerialName("r") val rootId: Long,

    @SerialName("b") val binyanId: Long,

    @SerialName("g") val gizrahId: List<Long>,

    @SerialName("p") val prepositionId: List<Long>,

    @SerialName("t") val translations: List<CreateVerbTranslationDto>
)


@Serializable
data class UpdateVerbDto(
    val id: Long,

    @SerialName("v") val value: String,
)
