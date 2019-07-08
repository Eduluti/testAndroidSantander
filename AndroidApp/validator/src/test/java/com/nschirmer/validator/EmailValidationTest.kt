package com.nschirmer.validator

import org.junit.Test

class EmailValidationTest {

    /**
     * Do a quick test of the email validation
     * **/
    @Test
    fun isValidatingEmails() {
        // Valid
        val isValid = EmailValidation("nschirmer.dev@gmail.com").isValid()

        // Invalid
        val isValid2 = EmailValidation("nschirmer.dev@gmail").isValid()
        val isValid3 = EmailValidation("nschirmer.dev@@gmail").isValid()
        val isValid4 = EmailValidation("@gmail.com").isValid()
    }


}