package com.leoschulmann.almi.dto

import com.leoschulmann.almi.entities.Verb
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerbDto(
    val id: Long,

    @SerialName("v")
    val value: String,

    @SerialName("ver")
    val version: Int,

    @SerialName("b")
    val binyan: BinyanDto,

    @SerialName("r")
    val root: RootDto,

    @SerialName("g")
    val gizrahs: List<GizrahDto>,

    @SerialName("p")
    val prepositions: List<PrepositionDto>,

    @SerialName("t")
    val translations: List<VerbTranslationDto>
)

fun Verb.toDto() = VerbDto(
    id.value,
    value,
    version,
    binyan.toDto(),
    root.toDto(),
    gizrahs.map { it.toDto() },
    prepositions.map { it.toDto() },
    translations.map { it.toDto() })
