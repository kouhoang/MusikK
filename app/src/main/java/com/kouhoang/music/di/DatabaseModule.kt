package com.kouhoang.music.di

import android.content.Context
import com.kouhoang.music.data.data_source.database.AppDatabase
import com.kouhoang.music.data.data_source.database.daos.FavouriteSongDAO
import com.kouhoang.music.data.data_source.database.daos.SongDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {


    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideSongDao(appDatabase: AppDatabase): SongDAO {
        return appDatabase.songDao()
    }

    @Provides
    @Singleton
    fun provideFavouriteSongDao(appDatabase: AppDatabase): FavouriteSongDAO {
        return appDatabase.favouriteSongDao()
    }
}