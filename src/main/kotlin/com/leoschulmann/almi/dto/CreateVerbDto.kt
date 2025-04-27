package com.leoschulmann.almi.dto

import com.leoschulmann.almi.enums.Lang
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateVerbDto(
    @SerialName("v")
    val value: String,

    @SerialName("r")
    val rootId: Long,

    @SerialName("b")
    val binyanId: Long,

    @SerialName("g")
    val gizrahId: List<Long>,

    @SerialName("p")
    val prepositionId: List<Long>,
    
    @SerialName("t")
    val translations: Map<Lang, String>
)
    