package com.example.stillpoint.data

import android.content.Context
import com.example.stillpoint.data.local.ContentDao
import com.example.stillpoint.data.local.ContentDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) /* This makes the dependencies live as long as the app does. */
object AppModule {

    @Provides
    @Singleton /* Ensure that there is only one instance of the database. */
    fun provideAppDatabase(@ApplicationContext context: Context) : ContentDatabase {
        return ContentDatabase.getDatabase(context)
    }

    @Provides
    @Singleton /* Ensure that there is only one instance of the DAO. */
    fun provideContentDao (database: ContentDatabase) : ContentDao = database.contentDao()

    @Provides
    @Singleton
    fun provideContentRepository(contentDao: ContentDao) : ContentRepository {
        return CachingContentRepository(contentDao)
    }
}