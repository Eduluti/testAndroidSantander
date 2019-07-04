package com.nschirmer.validator

import org.junit.Test

/**
 * The password validation rules are: Have at least one upper case character, one digit and special character
 * **/
class PasswordValidationTest {

    @Test
    fun isValidatingUpperCase() {
        // Valid
        val isValid = PasswordValidation("aB1$").isValid()

        // Invalid
        val isValid2 = PasswordValidation("ab1$").isValid()
    }


    @Test
    fun isValidatingSpecialCase() {
        // Valid
        val isValid = PasswordValidation("aB1$").isValid()

        // Invalid
        val isValid2 = PasswordValidation("aB1s").isValid()
    }


    @Test
    fun isValidatingDigit() {
        // Valid
        val isValid = PasswordValidation("aB1$").isValid()

        // Invalid
        val isValid2 = PasswordValidation("aBi$").isValid()
    }



}