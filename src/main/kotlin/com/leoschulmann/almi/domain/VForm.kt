package com.leoschulmann.almi.domain

import com.leoschulmann.almi.enums.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.SizedIterable


object VerbFormTable : LongIdTable("appdata.verbform") {
    val verb = reference("verb_id", VerbTable)
    val value = varchar("value", 255)
    val tense = enumerationByName("tense", 32, Tense::class)
    val person = enumerationByName("person", 32, GrammaticalPerson::class)
    val plurality = enumerationByName("plurality", 32, Plurality::class)
    val gender = enumerationByName("gender", 32, GrammaticalGender::class)
    val version = integer("version").default(0)
}

class VerbForm(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<VerbForm>(VerbFormTable)

    var verb: Verb by Verb referencedOn VerbFormTable.verb
    var value: String by VerbFormTable.value
    var version: Int by VerbFormTable.version
    var tense: Tense by VerbFormTable.tense
    var person: GrammaticalPerson by VerbFormTable.person
    var plurality: Plurality by VerbFormTable.plurality
    var gender: GrammaticalGender by VerbFormTable.gender
    val transliterations: SizedIterable<VerbFormTransliteration> by VerbFormTransliteration referrersOn VerbFormTranslitTable.verbForm

    fun toDto(): VerbFormDto =
        VerbFormDto(id.value, value, version, tense, person, plurality, gender, transliterations.map { it.toDto() })
}


@Serializable
data class VerbFormDto(
    val id: Long,
    @SerialName("v")
    val value: String,
    @SerialName("ver")
    val version: Int,
    @SerialName("t")
    val tense: Tense,
    @SerialName("p")
    val person: GrammaticalPerson,
    @SerialName("pl")
    val plurality: Plurality,
    @SerialName("g")
    val gender: GrammaticalGender,
    @SerialName("ts")
    val transliterations: List<TransliterationDto>
)

@Serializable
data class CreateVerbFormDto(
    @SerialName("v_id")
    val verbId: Long,

    @SerialName("v")
    val value: String,

    @SerialName("t")
    val tenseAndPerson: TensePerson,

    @SerialName("p")
    val pluralityGender: PluralityGender,

    @SerialName("ts")
    val transliterations: List<Pair<Lang, String>>
)

@Serializable
data class UpdateVerbFormDto(

    val id: Long,

    @SerialName("ver")
    val version: Int,

    @SerialName("v")
    val value: String,

    @SerialName("cts")
    val createTransliterations: List<Pair<Lang, String>>,

    @SerialName("uts")
    val updateTransliterations: List<UpdateVerbFormTransliteration>
)
