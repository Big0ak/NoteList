package com.example.notelist.network

import com.example.notelist.data.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

object ApiService {
    private const val BASE_URL = "https://v78qr.wiremockapi.cloud/notes"

    private val client = OkHttpClient()

    suspend fun fetchNotes(): List<Note>? = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(BASE_URL)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                // Проверяем статус-код, если это ошибка 400, выбрасываем исключение
                if (response.code == 400) {
                    throw Exception("Ошибка 400: Некорректный запрос к серверу")
                }
                return@withContext null // Для всех остальных ошибок
            }

            val responseData = response.body?.string() ?: return@withContext null
            parseNotes(responseData)
        }
    }


    private fun parseNotes(response: String): List<Note> {
        val notesArray = JSONObject(response).getJSONArray("notes")
        val notesList = mutableListOf<Note>()

        for (i in 0 until notesArray.length()) {
            val noteObject = notesArray.getJSONObject(i)
            val note = Note(
                id = noteObject.getInt("id"),
                title = noteObject.getString("title"),
                content = noteObject.getString("content"),
                timestamp = noteObject.getString("timestamp")
            )
            notesList.add(note)
        }
        return notesList
    }
}
