package com.iandrobot.androidtestpractice.repositories

import androidx.lifecycle.LiveData
import com.iandrobot.androidtestpractice.data.local.ShoppingDao
import com.iandrobot.androidtestpractice.data.local.ShoppingItem
import com.iandrobot.androidtestpractice.data.remote.PixabayAPI
import com.iandrobot.androidtestpractice.data.remote.responses.ImageResponse
import com.iandrobot.androidtestpractice.other.Resource
import javax.inject.Inject

class DefaultShoppingRepository @Inject constructor(
    private val shoppingDao: ShoppingDao,
    private val pixabayAPI: PixabayAPI
): ShoppingRepository {
    override suspend fun insertShoppingItem(shoppingItem: ShoppingItem) {
        shoppingDao.insertShoppingItem(shoppingItem)
    }

    override suspend fun deleteShoppingItem(shoppingItem: ShoppingItem) {
        shoppingDao.deleteShoppingItem(shoppingItem)
    }

    override fun observeAllShoppingItem(): LiveData<List<ShoppingItem>> {
        return shoppingDao.observeAllShoppingItems()
    }

    override fun observeTotalPrice(): LiveData<Float> {
        return shoppingDao.observeTotalPrice()
    }

    override suspend fun searchForImage(imageQuery: String): Resource<ImageResponse> {
        return try {
            val response = pixabayAPI.searchForImage(imageQuery)
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource.success(it)
                } ?: Resource.error("An unknown error occurred", null)
            } else {
                Resource.error("An unknown error occurred", null)
            }
        } catch(e: Exception) {
            Resource.error("Couldn't reach the server. Check internet connection", null)
        }
    }
}