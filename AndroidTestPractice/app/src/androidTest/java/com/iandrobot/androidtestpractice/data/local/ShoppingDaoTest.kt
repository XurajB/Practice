package com.iandrobot.androidtestpractice.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.iandrobot.androidtestpractice.getOrAwaitValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named

@RunWith(AndroidJUnit4::class)
@SmallTest // this means this is a unit test, integration is MediumTest, UI is LargeTest.
// Small test means no disk, db, file system, multi threads, sleep etc.. see table
@HiltAndroidTest // we want to inject dependencies
class ShoppingDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: ShoppingItemDatabase

    private lateinit var dao: ShoppingDao

    @Before
    fun setup() {
        /*
        // make sure we have fresh db for each test
        // and in ram, which will not save in db
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ShoppingItemDatabase::class.java
        ).allowMainThreadQueries().build()
        // allow main thread -> allow this from main thread just for test (other wise they should run in background thread)
        */
        hiltRule.inject()
        dao = database.shoppingDao()
    }

    @After
    fun teardown() {
        database.close() // close after each test
    }

    @Test
    fun insertShoppingItem() = runBlockingTest {
        // since the testing fun is suspend fun
        // runBlockingTest is optimized for tests, some methods are removed
        val shoppingItem = ShoppingItem("name", 1, 1f, "url", id = 1)
        dao.insertShoppingItem(shoppingItem)

        // this returns livedata but livedata runs in a different thread, to do that we use LiveDataUtilAndroid class provided by google
        val allShoppingItems = dao.observeAllShoppingItems().getOrAwaitValue() // we are using the extension method provided by that class ^
        assertThat(allShoppingItems).contains(shoppingItem)
        // even though we use runBlockingTest here (coz liveData), junit does not like that. to solve that we need to explicitly tell junit
        // that we want execute all code inside this test class one after another (in the same thread). so we create instant task executor rule
    }

    @Test
    fun deleteShoppingItem() = runBlockingTest {
        val shoppingItem = ShoppingItem("name", 1, 1f, "url", id = 1)
        dao.insertShoppingItem(shoppingItem)
        dao.deleteShoppingItem(shoppingItem)

        val allShoppingItems = dao.observeAllShoppingItems().getOrAwaitValue()
        assertThat(allShoppingItems).doesNotContain(shoppingItem)
    }

    @Test
    fun observeTotalPriceSum() = runBlockingTest {
        val shoppingItem = ShoppingItem("name", 1, 1f, "url", id = 1)
        val shoppingItem2 = ShoppingItem("name", 2, 1.5f, "url", id = 2)
        val shoppingItem3 = ShoppingItem("name", 3, 4f, "url", id = 3)
        dao.insertShoppingItem(shoppingItem)
        dao.insertShoppingItem(shoppingItem2)
        dao.insertShoppingItem(shoppingItem3)

        val totalPrice = dao.observeTotalPrice().getOrAwaitValue()
        assertThat(totalPrice).isEqualTo(1*1f + 2 * 1.5f + 3 * 4f)
    }
}