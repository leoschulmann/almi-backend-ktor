package com.leoschulmann.almi.dto

import com.leoschulmann.almi.entities.VerbForm
import com.leoschulmann.almi.enums.GrammaticalGender
import com.leoschulmann.almi.enums.GrammaticalPerson
import com.leoschulmann.almi.enums.Plurality
import com.leoschulmann.almi.enums.Tense
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerbFormDto(
    val id: Long,
    @SerialName("v")
    val value: String,
    @SerialName("ver")
    val version: Int,
    @SerialName("t")
    val tense: Tense,
    @SerialName("p")
    val person: GrammaticalPerson,
    @SerialName("pl")
    val plurality: Plurality,
    @SerialName("g")
    val gender: GrammaticalGender,
    @SerialName("ts")
    val transliterations: List<TransliterationDto>
)

fun VerbForm.toDto(): VerbFormDto =
    VerbFormDto(id.value, value, version, tense, person, plurality, gender, transliterations.map { it.toDto() })
