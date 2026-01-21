package com.brain

object LanguageDetector {

    enum class Lang(
        val code: String,
        val locale: String,
        val prompt: String
    ) {
        EN("en", "en-US", "Reply in friendly English."),
        HI("hi", "hi-IN", "हिंदी में दोस्ताना जवाब दो।"),
        BN("bn", "bn-IN", "বাংলায় বন্ধুত্বপূর্ণ উত্তর দাও।"),
        TA("ta", "ta-IN", "நண்பனாக தமிழில் பதில் அளி."),
        TE("te", "te-IN", "స్నేహపూర్వకంగా తెలుగులో సమాధానం ఇవ్వు."),
        MR("mr", "mr-IN", "मराठीत मैत्रीपूर्ण उत्तर दे."),
        UR("ur", "ur-IN", "دوستانہ انداز میں اردو میں جواب دو۔"),
        GU("gu", "gu-IN", "મૈત્રીપૂર્ણ રીતે ગુજરાતી માં જવાબ આપો."),
        PA("pa", "pa-IN", "ਪੰਜਾਬੀ ਵਿੱਚ ਦੋਸਤਾਨਾ ਜਵਾਬ ਦਿਓ।"),
        ES("es", "es-ES", "Responde en español de forma amigable.")
    }

    fun detect(text: String): Lang {
        val t = text.lowercase()

        return when {
            containsRange(t, 'अ', 'ह') -> Lang.HI
            containsRange(t, 'অ', 'হ') -> Lang.BN
            containsRange(t, 'அ', 'ஹ') -> Lang.TA
            containsRange(t, 'అ', 'హ') -> Lang.TE
            containsRange(t, 'अ', 'ह') && t.contains("मराठी") -> Lang.MR
            containsRange(t, 'ا', 'ي') -> Lang.UR
            containsRange(t, 'અ', 'હ') -> Lang.GU
            containsRange(t, 'ਅ', 'ਹ') -> Lang.PA
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
}
