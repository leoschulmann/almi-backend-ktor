package com.leoschulmann.almi.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

open class OrdinalEnumSerializer<E : Enum<E>>(
    serialName: String,
    private val values: Array<E>,
) : KSerializer<E> {
    override val descriptor = PrimitiveSerialDescriptor(serialName, PrimitiveKind.INT)
    override fun serialize(encoder: Encoder, value: E) = encoder.encodeInt(value.ordinal)
    override fun deserialize(decoder: Decoder): E = values[decoder.decodeInt()]
}

object TenseOrdinalSerializer : 
    OrdinalEnumSerializer<Tense>("Tense", Tense.entries.toTypedArray())
object GPersonOrdinalSerializer :
    OrdinalEnumSerializer<GrammaticalPerson>("GPerson", GrammaticalPerson.entries.toTypedArray())
object PluralityOrdinalSerializer : 
    OrdinalEnumSerializer<Plurality>("Plurality", Plurality.entries.toTypedArray())
object GGenderOrdinalSerializer :
    OrdinalEnumSerializer<GrammaticalGender>("GGender", GrammaticalGender.entries.toTypedArray())
