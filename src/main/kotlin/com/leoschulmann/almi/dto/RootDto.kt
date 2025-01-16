package com.leoschulmann.almi.dto

import com.leoschulmann.almi.entities.Root
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RootDto(val id: Long, @SerialName("r") val value: String, @SerialName("ver") val version: Int)

fun Root.toDto() = RootDto(id.value, value, version)
