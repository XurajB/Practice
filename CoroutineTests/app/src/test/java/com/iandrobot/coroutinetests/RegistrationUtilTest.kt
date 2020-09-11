package com.iandrobot.coroutinetests

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RegistrationUtilTest {

    @Test
    fun `empty unsername returns false`() {
        // we should not sue this method name in real classes but ok for unit tests, because junit will run it
        val result = RegistrationUtil.validateRegistrationInput(
            "",
            "123",
            "123"
        )
        // use truth lib instead of junit assert
        assertThat(result).isFalse()
    }

    @Test
    fun `valid unsername and password returns true`() {
        // we should not sue this method name in real classes but ok for unit tests, because junit will run it
        val result = RegistrationUtil.validateRegistrationInput(
            "Philip",
            "123",
            "123"
        )
        // use truth lib instead of junit assert
        assertThat(result).isTrue()
    }

    @Test
    fun `username already exists returns false`() {
        // we should not sue this method name in real classes but ok for unit tests, because junit will run it
        val result = RegistrationUtil.validateRegistrationInput(
            "Peter",
            "123",
            "123"
        )
        // use truth lib instead of junit assert
        assertThat(result).isFalse()
    }

    @Test
    fun `password empty returns false`() {
        // we should not sue this method name in real classes but ok for unit tests, because junit will run it
        val result = RegistrationUtil.validateRegistrationInput(
            "Hello",
            "",
            "123"
        )
        // use truth lib instead of junit assert
        assertThat(result).isFalse()
    }

    @Test
    fun `password repeated incorrectly returns false`() {
        // we should not sue this method name in real classes but ok for unit tests, because junit will run it
        val result = RegistrationUtil.validateRegistrationInput(
            "Hello",
            "",
            "1233"
        )
        // use truth lib instead of junit assert
        assertThat(result).isFalse()
    }

    @Test
    fun `password less than 2 chars returns false`() {
        // we should not sue this method name in real classes but ok for unit tests, because junit will run it
        val result = RegistrationUtil.validateRegistrationInput(
            "Joseph",
            "12",
            "12"
        )
        // use truth lib instead of junit assert
        assertThat(result).isFalse()
    }
}