package com.iandrobot.androidtestpractice.di

import android.content.Context
import androidx.room.Room
import com.iandrobot.androidtestpractice.data.local.ShoppingItemDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Named

@Module
@InstallIn(ApplicationComponent::class)
object TestAppModule {
    @Provides
    @Named("test_db")
    // we don't need @Singleton here because we want each unit test to have new instance of this class
    fun providesInMemoryDb(@ApplicationContext context: Context) =
        Room.inMemoryDatabaseBuilder(context, ShoppingItemDatabase::class.java)
            .allowMainThreadQueries()
            .build()
}