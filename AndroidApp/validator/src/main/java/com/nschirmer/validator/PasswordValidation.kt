package com.nschirmer.validator


class PasswordValidation (private val password: String) {

    /** @return If the password match the system parameters **/
    fun isValid(): Boolean {
        return  hasAtLeastOneNumber() && hasAtLeastOneSpecialCase() && hasAtLeastOneUpperCase()
    }


    /** @return If the password has at least one digit in it **/
    private fun hasAtLeastOneNumber(): Boolean {
        return password.contains(Regex("\\d"))
    }


    /** @return If the password has at least one upper case character in it **/
    private fun hasAtLeastOneUpperCase(): Boolean {
        return password.contains(Regex("[A-Z]"))
    }


    /** @return If the password has at least one special character in it
     *      e.g. !@#$%ˆˆ*().....
     * **/
    private fun hasAtLeastOneSpecialCase(): Boolean {
        return password.contains(Regex("\\W"))
    }

}