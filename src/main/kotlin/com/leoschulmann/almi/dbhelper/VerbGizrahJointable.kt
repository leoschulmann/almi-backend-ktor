package com.leoschulmann.almi.dbhelper

import com.leoschulmann.almi.domain.GizrahTable
import com.leoschulmann.almi.domain.VerbTable
import org.jetbrains.exposed.sql.Table

object VerbGizrahJointable : Table("appdata.verb_gizrah") {
    val verb = reference("verb_id", VerbTable)
    val gizrah = reference("gizrah_id", GizrahTable)
    override val primaryKey = PrimaryKey(verb, gizrah)
}
    