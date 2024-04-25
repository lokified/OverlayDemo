package com.loki.overlaydemo.notes

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 2,
    entities = [Note::class],
    exportSchema = false
)
abstract class NotesDb : RoomDatabase() {

    abstract val noteDao: NoteDao

    companion object {
        const val DATABASE_NAME = "note_db"
    }
}