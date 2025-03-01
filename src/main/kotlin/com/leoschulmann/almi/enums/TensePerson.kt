package com.leoschulmann.almi.enums

enum class TensePerson {
    INFINITIVE,
    PRESENT,
    PAST_FIRST_PERSON,
    PAST_SECOND_PERSON,
    PAST_THIRD_PERSON,
    FUTURE_FIRST_PERSON,
    FUTURE_SECOND_PERSON,
    FUTURE_THIRD_PERSON,
    IMPERATIVE;

    fun getTense(): Tense {
        return when (this) {
            INFINITIVE -> Tense.INFINITIVE
            PRESENT -> Tense.PRESENT
            PAST_FIRST_PERSON, PAST_SECOND_PERSON, PAST_THIRD_PERSON -> Tense.PAST
            FUTURE_FIRST_PERSON, FUTURE_SECOND_PERSON, FUTURE_THIRD_PERSON -> Tense.FUTURE
            IMPERATIVE -> Tense.IMPERATIVE
        }
    }

    fun getPerson(): GrammaticalPerson {
        return when (this) {
            INFINITIVE, PRESENT, IMPERATIVE -> GrammaticalPerson.NONE
            FUTURE_FIRST_PERSON, PAST_FIRST_PERSON -> GrammaticalPerson.FIRST
            FUTURE_SECOND_PERSON, PAST_SECOND_PERSON -> GrammaticalPerson.SECOND
            FUTURE_THIRD_PERSON, PAST_THIRD_PERSON -> GrammaticalPerson.THIRD
        }
    }
}