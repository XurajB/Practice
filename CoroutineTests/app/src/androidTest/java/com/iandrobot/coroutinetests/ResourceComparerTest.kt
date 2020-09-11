package com.iandrobot.coroutinetests

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class ResourceComparerTest {
    // it is a bad idea to have a same member variable used in different unit tests
    // because we are not testing each test in isolation because we share the same class
    // if we have a counter that updates in each method call then that affects other test cases, this causes flaky tests
    // so we need a new instance for each tests
    //private val resourceComparer = ResourceComparer()

    // this is one way to do this, initialize in each test cases
    // but if we have 30 test cases, we will have to do that 30 times which is a lot of boiler plate
    // instead we use Before function provided by junit
    private lateinit var resourceComparer: ResourceComparer

    @Before
    fun setup() {
        // logic to execute before every test case
        resourceComparer = ResourceComparer()
    }

    @After
    fun teardown() {
        // destroy objects after unit tests, like room database close after every test case
    }

    @Test
    fun stringResourceSameAsGiven_returnsTrue() {
        //resourceComparer = ResourceComparer()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = resourceComparer.isEqual(context, R.string.app_name, "CoroutineTests")
        assertThat(result).isTrue()
    }

    @Test
    fun stringResourceNotSameAsGiven_returnsFalse() {
        //resourceComparer = ResourceComparer()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = resourceComparer.isEqual(context, R.string.app_name, "Hello")
        assertThat(result).isFalse()
    }
}