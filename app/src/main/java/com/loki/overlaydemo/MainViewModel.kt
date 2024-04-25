package com.loki.overlaydemo

import android.content.Context
import android.content.IntentFilter
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.loki.overlaydemo.notes.Note
import com.loki.overlaydemo.receiver.NotesReceiver
import com.loki.overlaydemo.receiver.NotesReceiver.Companion.NOTES_RECEIVER_ACTION
import kotlinx.coroutines.launch

class MainViewModel(
    private val appModule: AppModule
): ViewModel() {


    private val updateReceiver = NotesReceiver { getNotes() }

    var noteLists = mutableStateOf<List<Note>>(emptyList())
        private set

    init {
        Log.d("********", noteLists.value.toString())

        appModule.appContext.registerReceiver(
            updateReceiver, IntentFilter(NOTES_RECEIVER_ACTION), Context.RECEIVER_NOT_EXPORTED
        )
        getNotes()
    }

    fun getNotes() = viewModelScope.launch {
         appModule.noteRepository.getNotes().collect {
             noteLists.value = it
         }
    }

    override fun onCleared() {
        super.onCleared()
        appModule.appContext.unregisterReceiver(updateReceiver)
    }
}

class MainViewModelProvider(private val appModule: AppModule): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(appModule) as T
    }
}