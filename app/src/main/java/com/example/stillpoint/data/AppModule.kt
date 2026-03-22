package com.example.stillpoint.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.stillpoint.data.local.ContentDao
import com.example.stillpoint.data.local.ContentDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Module
@InstallIn(SingletonComponent::class) /* This makes the dependencies live as long as the app does. */
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(dataStore: DataStore<Preferences>): UserPreferencesRepository {
        return DataStoreUserPreferencesRepository(dataStore)
    }

    @Provides
    @Singleton
    fun provideWebContentExtractor(): WebContentExtractor {
        return ReadabilityContentExtractor()
    }

    @Provides
    @Singleton /* Ensure that there is only one instance of the database. */
    fun provideAppDatabase(@ApplicationContext context: Context) : ContentDatabase {
        return ContentDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideContentDao (database: ContentDatabase) : ContentDao = database.contentDao()

    @Provides
    @Singleton
    fun provideContentRepository(
        contentDao: ContentDao,
        webContentExtractor: WebContentExtractor
    ) : ContentRepository {
        return CachingContentRepository(contentDao, webContentExtractor)
    }
}