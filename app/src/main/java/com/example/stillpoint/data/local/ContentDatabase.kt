package com.example.stillpoint.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ContentItem::class], version = 3, exportSchema = true)
abstract class ContentDatabase : RoomDatabase() {
    abstract fun contentDao() : ContentDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1,2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE content_items ADD COLUMN isArchived INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_2_3 = object : Migration(2,3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE content_items ADD COLUMN languageCode TEXT DEFAULT 'en'")
            }
        }
        @Volatile
        private var Instance: ContentDatabase? = null
        fun getDatabase(context: Context): ContentDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ContentDatabase::class.java, "stillpoint_database")
                    .fallbackToDestructiveMigration(true)
                    .addMigrations(MIGRATION_2_3)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}