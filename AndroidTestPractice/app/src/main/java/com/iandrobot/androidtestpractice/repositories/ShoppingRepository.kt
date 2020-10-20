package com.iandrobot.androidtestpractice.repositories

import androidx.lifecycle.LiveData
import com.iandrobot.androidtestpractice.data.local.ShoppingItem
import com.iandrobot.androidtestpractice.data.remote.responses.ImageResponse
import com.iandrobot.androidtestpractice.other.Resource

interface ShoppingRepository {

    suspend fun insertShoppingItem(shoppingItem: ShoppingItem)

    suspend fun deleteShoppingItem(shoppingItem: ShoppingItem)

    fun observeAllShoppingItem(): LiveData<List<ShoppingItem>>

    fun observeTotalPrice(): LiveData<Float>

    suspend fun searchForImage(imageQuery: String): Resource<ImageResponse>
}