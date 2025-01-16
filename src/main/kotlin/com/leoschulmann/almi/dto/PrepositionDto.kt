package com.leoschulmann.almi.dto

import com.leoschulmann.almi.entities.Preposition
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrepositionDto(val id: Long, @SerialName("p") val value: String, @SerialName("ver") val version: Int)

fun Preposition.toDto() = PrepositionDto(id.value, value, version)
