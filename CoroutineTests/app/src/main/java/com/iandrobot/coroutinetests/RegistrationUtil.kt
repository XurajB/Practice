package com.iandrobot.coroutinetests

// this is an object
// which is a singleton in kotlin, which means constructors are not allowed
object RegistrationUtil {

    private val existingUsers = listOf("Peter", "Carl")

    /**
     * the input is not valid if
     * username/password is empty
     * username is already taken
     * confirmedPassword is not equal to real password
     * password contains less than 2 chars
     */
    // to generate a test class to this, right click > generate > Test..
    fun validateRegistrationInput(
        username: String,
        password: String,
        confirmedPassword: String
    ): Boolean {
        if (username.isEmpty() || password.isEmpty()) {
            return false
        }
        if (username in existingUsers) {
            return false
        }
        if (password != confirmedPassword) {
            return false
        }
        if (password.length <= 2) {
            return false
        }
        return true
    }
}