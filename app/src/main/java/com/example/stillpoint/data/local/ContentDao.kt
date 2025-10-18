package com.example.stillpoint.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {
    @Insert
    suspend fun insertItem(item : ContentItem)

    @Query("SELECT * FROM content_items WHERE isArchived = 0 ORDER BY addedAt DESC")
    fun getAllItems() : Flow<List<ContentItem>>

    @Query("SELECT * FROM content_items WHERE isArchived = 1 ORDER BY addedAt DESC")
    fun getArchivedItems() : Flow<List<ContentItem>>

    @Delete
    suspend fun deleteMultipleItems(items: List<ContentItem>)
    @Delete
    suspend fun deleteItem(item: ContentItem)

    @Update // <-- Add the Update function
    suspend fun updateItem(item: ContentItem)
}