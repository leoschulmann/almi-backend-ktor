package com.leoschulmann.almi.dbhelper

import com.leoschulmann.almi.domain.PrepositionTable
import com.leoschulmann.almi.domain.VerbTable
import org.jetbrains.exposed.sql.Table

object VerbPrepositionJointable : Table("appdata.verb_preposition") {
    val verb = reference("verb_id", VerbTable)
    val preposition = reference("preposition_id", PrepositionTable)
    override val primaryKey = PrimaryKey(verb, preposition)
}

//create table appdata.verb_preposition
//(
//    verb_id        bigint not null
//        constraint verb_preposition_verb_id_fk
//            references appdata.verb,
//    preposition_id bigint not null
//        constraint verb_preposition_preposition_id_fk
//            references appdata.preposition,
//    constraint verb_preposition_pk
//        primary key (preposition_id, verb_id)
//);
//
//alter table appdata.verb_preposition
//    owner to postgres;