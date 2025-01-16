package com.leoschulmann.almi.entities

import kotlinx.serialization.Serializable

@Serializable
data class PagedResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
) {
    @Suppress("unused")
    val totalPages: Long = (totalElements / size) + if (totalElements % size > 0) 1 else 0
}
