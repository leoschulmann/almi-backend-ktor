package com.leoschulmann.almi.dto

import com.leoschulmann.almi.entities.Binyan
import kotlinx.serialization.Serializable

@Serializable
data class BinyanDto(
    val id: Long,
    val value: String,
    val version: Int
)

fun Binyan.toDto() = BinyanDto(id.value, value, version)
