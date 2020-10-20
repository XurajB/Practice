package com.iandrobot.androidtestpractice.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.iandrobot.androidtestpractice.MainCoroutineRule
import com.iandrobot.androidtestpractice.getOrAwaitValueTest
import com.iandrobot.androidtestpractice.other.Constants
import com.iandrobot.androidtestpractice.other.Status
import com.iandrobot.androidtestpractice.repositories.FakeShoppingRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// viewmodel is not android component so we are good for jvm test
class ShoppingViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule() // it will make sure that all the code is executed in  the same thread. because we are using livedata that will run in diff thread

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule() // because the main looper is not available for local tests, the MainCoroutineRule will replace the main dispatcher with a test dispatcher

    private lateinit var viewModel: ShoppingViewModel

    @Before
    fun setup() {
        viewModel = ShoppingViewModel(FakeShoppingRepository())
    }

    @Test
    fun `insert shopping item with empty field, returns error`() {
        viewModel.insertShoppingItem("name", "", "3")
        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()
        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too long name, returns error`() {
        // instead of hard coding 21 chars string then later if we increase the limit to 30, this test case will pass
        val string = buildString {
            for(i in 1..Constants.MAX_NAME_LENGTH + 1) {
                append('d')
            }
        }
        viewModel.insertShoppingItem(string, "6", "3")
        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()
        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too long price, returns error`() {
        val string = buildString {
            for(i in 1..Constants.MAX_PRICE_LENGTH + 1) {
                append(1)
            }
        }
        viewModel.insertShoppingItem("name", "6", string)
        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()
        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with too high amount, returns error`() {
        viewModel.insertShoppingItem("name", "9999999999999999999999", "1.0")
        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()
        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert shopping item with valid input, returns success`() {
        // this method uses coroutine to insert into db, the jvm test does not have excess to Main dispatcher
        // so we will have to use our own coroutine rule -> MainCoroutineRule
        viewModel.insertShoppingItem("name", "9", "3.0")
        val value = viewModel.insertShoppingItemStatus.getOrAwaitValueTest()
        assertThat(value.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }
}