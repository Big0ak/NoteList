package com.example.notelist.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notelist.data.Note

@Composable
fun NoteItem(
    note: Note,
    onClick: () -> Unit,            // Обработчик нажатия на заметку для открытия деталей
    onStatusChange: (Boolean) -> Unit // Обработчик изменения статуса выполненности
) {

    val formattedTimestamp = if (note.timestamp.isNullOrBlank()) {
        "Дата не указана"
    } else {
        note.timestamp.replace("T", " ").take(19) // Убираем символ 'T' и оставляем дату и время
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = formattedTimestamp,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Checkbox(
            checked = note.finished,
            onCheckedChange = { isChecked ->
                onStatusChange(isChecked) // Передача нового состояния
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onClick) {
            Text(text = "Детали")
        }
    }
}
