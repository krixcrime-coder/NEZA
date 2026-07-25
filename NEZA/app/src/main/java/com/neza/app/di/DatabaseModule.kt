package com.neza.app.di

import android.content.Context
import androidx.room.Room
import com.neza.app.data.local.ChatDao
import com.neza.app.data.local.ChatDatabase
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
    fun provideChatDatabase(@ApplicationContext context: Context): ChatDatabase =
        Room.databaseBuilder(context, ChatDatabase::class.java, "neza_chat.db").build()

    @Provides
    fun provideChatDao(database: ChatDatabase): ChatDao = database.chatDao()
}
