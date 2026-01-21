package com.brain

import java.util.Locale

object LanguageDetector {

    enum class Lang(
        val code: String,
        val locale: Locale,
        val prompt: String
    ) {
        EN("en", Locale.US, "Reply in friendly English."),
        HI("hi", Locale("hi", "IN"), "हिंदी में दोस्ताना जवाब दो।"),
        BN("bn", Locale("bn", "IN"), "বাংলায় বন্ধুত্বপূর্ণ উত্তর দাও।"),
        TA("ta", Locale("ta", "IN"), "நண்பனாக தமிழில் பதில் அளி."),
        TE("te", Locale("te", "IN"), "స్నేహపూర్వకంగా తెలుగులో సమాధానం ఇవ్వు."),
        MR("mr", Locale("mr", "IN"), "मराठीत मैत्रीपूर्ण उत्तर दे."),
        UR("ur", Locale("ur", "IN"), "دوستانہ انداز میں اردو میں جواب دو۔"),
        GU("gu", Locale("gu", "IN"), "મૈત્રીપૂર્ણ રીતે ગુજરાતી માં જવાબ આપો."),
        PA("pa", Locale("pa", "IN"), "ਪੰਜਾਬੀ ਵਿੱਚ ਦੋਸਤਾਨਾ ਜਵਾਬ ਦਿਓ।"),
        ES("es", Locale("es", "ES"), "Responde en español de forma amigable.")
    }

    fun detect(text: String): Lang {
        val t = text.lowercase()

        return when {
            // Marathi (Devanagari + keywords)
            containsRange(t, 'अ', 'ह') && looksMarathi(t) -> Lang.MR

            // Hindi (Devanagari)
            containsRange(t, 'अ', 'ह') -> Lang.HI

            // Bengali
            containsRange(t, 'অ', 'হ') -> Lang.BN

            // Tamil
            containsRange(t, 'அ', 'ஹ') -> Lang.TA

            // Telugu
            containsRange(t, 'అ', 'హ') -> Lang.TE

            // Urdu
            containsRange(t, 'ا', 'ي') -> Lang.UR

            // Gujarati
            containsRange(t, 'અ', 'હ') -> Lang.GU

            // Punjabi (Gurmukhi)
            containsRange(t, 'ਅ', 'ਹ') -> Lang.PA

            // Spanish (heuristic)
            looksSpanish(t) -> Lang.ES

            else -> Lang.EN
        }
    }

    private fun containsRange(text: String, start: Char, end: Char): Boolean {
        return text.any { it in start..end }
    }

    private fun looksSpanish(text: String): Boolean {
        val hints = listOf("¿", "¡", "ción", "está", "hola", "gracias")
        return hints.any { text.contains(it) }
    }

    private fun looksMarathi(text: String): Boolean {
        val hints = listOf(
            "आहे", "नाही", "काय", "का", "माझं", "तुम्ही", "आम्ही"
        )
        return hints.any { text.contains(it) }
    }
}
