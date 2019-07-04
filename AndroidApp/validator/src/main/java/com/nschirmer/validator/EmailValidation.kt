package com.nschirmer.validator

import androidx.core.util.PatternsCompat.EMAIL_ADDRESS


class EmailValidation (private val email: String) {

    /** @return If is a valid email **/
    fun isValid(): Boolean {
        return EMAIL_ADDRESS.matcher(email).matches()
    }

}