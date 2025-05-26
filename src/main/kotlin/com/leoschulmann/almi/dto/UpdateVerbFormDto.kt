package com.leoschulmann.almi.dto

import com.leoschulmann.almi.enums.Lang
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateVerbFormDto(

    val id: Long,
    
    @SerialName("ver")
    val version: Int,

    @SerialName("v")
    val value: String,

    @SerialName("cts")
    val createTransliterations: List<Pair<Lang, String>>,
    
    @SerialName("uts")
    val updateTransliterations: List<UpdateVerbFormTransliteration>
)
