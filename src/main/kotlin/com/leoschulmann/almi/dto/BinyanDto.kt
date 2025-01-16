package com.leoschulmann.almi.dto

import com.leoschulmann.almi.entities.Binyan
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BinyanDto(val id: Long, @SerialName("b") val value: String, @SerialName("ver") val version: Int)

fun Binyan.toDto() = BinyanDto(id.value, value, version)
