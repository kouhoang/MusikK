package com.kouhoang.music.di

import android.content.Context
import com.kouhoang.music.data.data_source.app_data.DataLocalManager
import com.kouhoang.music.data.data_source.app_data.MySharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDataModule {

    @Provides
    @Singleton
    fun provideMySharedPreferences(@ApplicationContext context: Context): MySharedPreferences {
        return MySharedPreferences(context)
    }

    @Provides
    @Singleton
    fun provideDataLocalManager(sharedPreferences: MySharedPreferences): DataLocalManager {
        return DataLocalManager(sharedPreferences)
    }
}