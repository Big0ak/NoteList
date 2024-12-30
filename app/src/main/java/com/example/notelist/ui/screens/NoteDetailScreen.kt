package com.example.notelist.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notelist.data.Note



@Composable
fun NoteDetailScreen(note: Note) {
// Форматируем поле timestamp вручную, проверяя его на null или пустоту
    val formattedTimestamp = if (note.timestamp.isNullOrBlank()) {
        "Дата не указана"
    } else {
        note.timestamp.replace("T", " ").take(19) // Убираем символ 'T' и оставляем дату и время
    }



    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Детали заметки", style = MaterialTheme.typography.headlineMedium)
            Text(text = "Заголовок: ${note.title}")
            Text(text = "Содержание: ${note.content}")
            Text(text = "Дата и время: $formattedTimestamp")
        }
    }
}

