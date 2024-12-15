package com.leoschulmann.almi.tables

import org.jetbrains.exposed.sql.Table

object VerbGizrahJointable : Table("verb_gizrah") {
    val verb = reference("verb_id", VerbTable)
    val gizrah = reference("gizrah_id", GizrahTable)
    override val primaryKey = PrimaryKey(verb, gizrah)
}
    