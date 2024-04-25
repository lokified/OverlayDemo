package com.loki.overlaydemo

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.loki.overlaydemo.notes.NoteRepository
import com.loki.overlaydemo.notes.NoteRepositoryImpl
import com.loki.overlaydemo.notes.NotesDb

interface AppModule {
    val noteDb: NotesDb
    val noteRepository: NoteRepository
    val appContext: Context
}

class AppModuleImpl(private val application: Application) : AppModule {
    override val noteDb: NotesDb by lazy {
        Room.databaseBuilder(
            application,
            NotesDb::class.java,
            NotesDb.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    override val noteRepository: NoteRepository by lazy {
        NoteRepositoryImpl(noteDb.noteDao)
    }

    override val appContext: Context = application
}