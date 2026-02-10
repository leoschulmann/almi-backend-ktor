package com.leoschulmann.almi.dbhelper

import com.leoschulmann.almi.domain.GizrahTable
import com.leoschulmann.almi.domain.VerbTable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object VerbGizrahJointable : Table("appdata.verb_gizrah") {
    val verb = reference("verb_id", VerbTable)
    val gizrah = reference("gizrah_id", GizrahTable)
    override val primaryKey = PrimaryKey(verb, gizrah)

    fun getPaginatedGizrahLinks(limit: Int, offset: Long): List<VerbGizrahLinkDto> = transaction {
        selectAll()
            .limit(limit)
            .offset(offset)
            .map { VerbGizrahLinkDto(it[verb].value, it[gizrah].value) }
    }

    fun countGizrahLinks(): Long = transaction { selectAll().count() }
}

@Serializable
data class VerbGizrahLinkDto(

    @SerialName("v")
    val verbId: Long,

    @SerialName("g")
    val gizrahId: Long)


    