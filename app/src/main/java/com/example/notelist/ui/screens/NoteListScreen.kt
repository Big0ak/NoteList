package com.example.notelist.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import com.example.notelist.data.Note
import com.example.notelist.ui.components.NoteItem
import kotlinx.coroutines.launch

@Composable
fun NoteListScreen(
    notes: List<Note>,                     // Список заметок
    onNoteClick: (Note) -> Unit,           // Обработчик нажатия на заметку
    onStatusChange: (Note, Boolean) -> Unit, // Обработчик изменения статуса
    errorMessage: String? = null,          // Сообщение об ошибке
    onDismissError: () -> Unit = {}        // Сброс ошибки
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Отображение сообщения об ошибке, если оно есть
    if (errorMessage != null) {
        LaunchedEffect(errorMessage) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(errorMessage)
                onDismissError()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Список заметок
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notes.size) { index ->
                val note = notes[index]
                NoteItem(
                    note = note,
                    onClick = { onNoteClick(note) },           // Переход к деталям
                    onStatusChange = { isChecked ->            // Обновление статуса
                        onStatusChange(note, isChecked)
                    }
                )
            }
        }

        // Отображение Snackbar
        SnackbarHost(hostState = snackbarHostState)
    }
}
