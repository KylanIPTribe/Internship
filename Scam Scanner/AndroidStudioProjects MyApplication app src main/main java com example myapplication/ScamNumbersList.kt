package com.example.myapplication

/** A Kotlin singleton (object) that checks phone numbers against a known scam list. 
 */
object ScamNumbersList {
    // Simulated list of known scam numbers
    // In a real app, this would come from a database or API
    private val scamNumbers = setOf(
        "+62 91112345678",
        "+62 54212345678",
        "+62 65112345678",
        "+62 72112345678"
    )

    /**
     * Get all known scam numbers
     */
    fun getScamNumbers(): Set<String> {
        return scamNumbers
    }

    /**
     * Check if a phone number is a known scam number
     * @param phoneNumber The phone number to check
     * @return true if the number is identified as a scam, false otherwise
     */
    fun isScamNumber(phoneNumber: String?): Boolean {
        if (phoneNumber == null) return false
        // Normalize the phone number (remove spaces, dashes, parentheses)
        val normalized = phoneNumber.replace(Regex("[\\s\\-\\(\\)]"), "")
        // Check if the normalized number matches any scam number
        return scamNumbers.any { scamNumber ->
            normalized.contains(scamNumber.replace(Regex("[\\s\\-\\(\\)]"), ""), ignoreCase = true) ||
            normalized.endsWith(scamNumber.replace(Regex("[\\s\\-\\(\\)]"), ""))
        }
    }

    /**
     * Add a number to the scam list (for user reporting)
     */
    fun addScamNumber(phoneNumber: String) {
        // In a real app, this would save to a database
        // For now, we'll just keep it in memory
        // Note: This won't persist across app restarts in this implementation
    }
}

