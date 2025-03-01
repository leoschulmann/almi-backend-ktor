package com.leoschulmann.almi.dto

import com.leoschulmann.almi.enums.Lang
import com.leoschulmann.almi.enums.PluralityGender
import com.leoschulmann.almi.enums.TensePerson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateVerbFormDto(
    @SerialName("v_id")
    val verbId: Long,

    @SerialName("v")
    val value: String,

    @SerialName("t")
    val tenseAndPerson: TensePerson,

    @SerialName("p")
    val pluralityGender: PluralityGender,

    @SerialName("ts")
    val transliterations: List<Pair<Lang, String>>
)
