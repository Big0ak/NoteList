package com.example.notelist.data

import android.content.Context
import com.example.notelist.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

class NoteRepository(private val context: Context) {

    // SharedPreferences для локального хранения заметок
    private val sharedPreferences = context.getSharedPreferences("note_preferences", Context.MODE_PRIVATE)

    // Метод для получения списка заметок
    suspend fun getNotes(): List<Note> = withContext(Dispatchers.IO) {
        try {
            // Получаем заметки с сервера
            val notes = ApiService.fetchNotes()
            if (notes != null) {
                saveNotesToLocal(notes) // Сохраняем их в локальное хранилище
                return@withContext notes
            }
        } catch (e: Exception) {
            throw Exception("Ошибка при загрузке заметок: ${e.message}")
        }
        // Если сервер недоступен, возвращаем данные из локального хранилища
        return@withContext getNotesFromLocal()
    }

    // Сохранение заметок в локальное хранилище
    private fun saveNotesToLocal(notes: List<Note>) {
        val editor = sharedPreferences.edit()
        val json = notes.joinToString(prefix = "[", postfix = "]") { note ->
            """
            {
                "id": ${note.id},
                "title": "${note.title}",
                "content": "${note.content}",
                "timestamp": "${note.timestamp}",
                "finished": ${note.finished}
            }
            """
        }
        editor.putString("notes", json)
        editor.apply()
    }

    // Получение заметок из локального хранилища
    private fun getNotesFromLocal(): List<Note> {
        val noteJson = sharedPreferences.getString("notes", "[]") ?: "[]"
        val noteList = mutableListOf<Note>()

        try {
            val jsonArray = JSONArray(noteJson)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                noteList.add(
                    Note(
                        id = jsonObject.getInt("id"),
                        title = jsonObject.getString("title"),
                        content = jsonObject.getString("content"),
                        timestamp = jsonObject.getString("timestamp"),
                        finished = jsonObject.getBoolean("finished")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return noteList
    }

    // Метод для обновления статуса заметки
    suspend fun updateNoteStatus(note: Note, finished: Boolean) {
        withContext(Dispatchers.IO) {
            // Обновляем локальное состояние заметок
            val updatedNotes = getNotesFromLocal().map {
                if (it.id == note.id) it.copy(finished = finished) else it
            }
            saveNotesToLocal(updatedNotes)
        }
    }
}
