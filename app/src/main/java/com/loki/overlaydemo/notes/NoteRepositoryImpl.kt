package com.loki.overlaydemo.notes

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

interface NoteRepository {
    fun addNote(note: Note)
    fun getNotes(): Flow<List<Note>>
}

class NoteRepositoryImpl(
    private val notesDao: NoteDao
): NoteRepository {

    override fun addNote(note: Note) {
        CoroutineScope(Dispatchers.IO).launch {
            notesDao.addNote(note)
        }
    }

    override fun getNotes(): Flow<List<Note>> {
        return notesDao.getNotes()
    }
}