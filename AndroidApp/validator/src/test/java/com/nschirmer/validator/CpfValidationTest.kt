package com.nschirmer.validator

import org.junit.Test

class CpfValidationTest {

    /** Check if the validation is accepting ###.###.###-## and ########### **/
    @Test
    fun isValidatingAnyCpf() {
        // Valid
        val isValid = CpfValidation("164.934.070-28").isValid()
        val isValid2 = CpfValidation("16493407028").isValid()

        // Invalid
        val isValid3 = CpfValidation("123.456.789-01").isValid()
        val isValid4 = CpfValidation("12345678901").isValid()
    }


    /**
     * Validate if is return false to blocked CPF
     * 111.111.111-11, 222.222.222-22 ......
     * **/
    @Test
    fun isValidatingBlockedCpf() {
        // Valid
        val isValid = CpfValidation("164.934.070-28").isValid()

        // Invalid
        val isValid2 = CpfValidation("111.111.111-11").isValid()
    }


    /** Check if the is validating the cpf size (11) **/
    @Test
    fun isValidatingSize() {
        // Valid
        val isValid = CpfValidation("164.934.070-28").isValid()

        // Invalid
        val isValid2 = CpfValidation("164.934.070").isValid()
    }


    /**
     * Check if is valiating the symbols in cpf like:
     * @example  ###.###.###-##
     * **/
    @Test
    fun isValidatingSymbols() {
        // Valid
        val isValid = CpfValidation("164.934.070-28").isValid()

        // Invalid
        val isValid2 = CpfValidation("164.934-070.28").isValid()
    }


}