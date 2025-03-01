package com.leoschulmann.almi.enums

enum class PluralityGender {
    NONE,
    SINGULAR_MASCULINE,
    SINGULAR_FEMININE,
    SINGULAR_NOGENDER,
    PLURAL_MASCULINE,
    PLURAL_FEMININE,
    PLURAL_NOGENDER;

    fun getPlurality(): Plurality {
        return when (this) {
            SINGULAR_MASCULINE, SINGULAR_FEMININE, SINGULAR_NOGENDER ->
                Plurality.SINGULAR

            PLURAL_MASCULINE, PLURAL_FEMININE, PLURAL_NOGENDER ->
                Plurality.PLURAL

            NONE -> Plurality.NONE
        }
    }

    fun getGrammaticalGender(): GrammaticalGender {
        return when (this) {
            SINGULAR_MASCULINE, PLURAL_MASCULINE -> GrammaticalGender.MASCULINE
            SINGULAR_FEMININE, PLURAL_FEMININE -> GrammaticalGender.FEMININE
            SINGULAR_NOGENDER, PLURAL_NOGENDER, NONE -> GrammaticalGender.NONE
        }
    }
}