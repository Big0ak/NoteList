package com.example.notelist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notelist.data.Note
import com.example.notelist.data.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class NoteViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadNotes()
        startPeriodicUpdates() // Запускаем периодическое обновление
    }

    private fun loadNotes() {
        viewModelScope.launch {
            try {
                _notes.value = repository.getNotes()
            } catch (e: Exception) {
                handleError(e.message ?: "Ошибка загрузки заметок")
            }
        }
    }

    fun updateNoteStatus(note: Note, completed: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateNoteStatus(note, completed)
                _notes.value = _notes.value.map {
                    if (it.id == note.id) it.copy(finished = completed) else it
                }
            } catch (e: Exception) {
                handleError(e.message ?: "Ошибка обновления статуса заметки")
            }
        }
    }

    private fun handleError(message: String) {
        _error.value = message
    }

    fun dismissError() {
        _error.value = null
    }

    // Метод для запуска периодического обновления
    private fun startPeriodicUpdates() {
        viewModelScope.launch {
            while (true) {
                delay(60000) // Задержка в 60 секунд
                try {
                    val updatedNotes = repository.getNotes()
                    _notes.value = updatedNotes
                } catch (e: Exception) {
                    handleError(e.message ?: "Ошибка обновления заметок")
                }
            }
        }
    }
}
