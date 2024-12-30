package com.example.notelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notelist.data.Note
import com.example.notelist.data.NoteRepository
import com.example.notelist.ui.screens.NoteDetailScreen
import com.example.notelist.ui.screens.NoteListScreen
import com.example.notelist.viewmodel.NoteViewModel
import com.example.notelist.viewmodel.NoteViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Создаем репозиторий и ViewModel
        val repository = NoteRepository(applicationContext)
        viewModel = ViewModelProvider(this, NoteViewModelFactory(repository))[NoteViewModel::class.java]


        setContent {
            val navController = rememberNavController()
            val notes = viewModel.notes.collectAsState().value
            val errorMessage = viewModel.error.collectAsState().value

            // Навигация между экранами
            NavHost(navController = navController, startDestination = "note_list") {
                composable("note_list") {
                    NoteListScreen(
                        notes = notes,
                        onNoteClick = { note ->
                            navController.navigate("note_detail/${note.id}")
                        },
                        onStatusChange = { note, completed ->
                            viewModel.updateNoteStatus(note, completed)
                        },
                        errorMessage = errorMessage,
                        onDismissError = { viewModel.dismissError() }
                    )
                }
                composable("note_detail/{noteId}") { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
                    val selectedNote = notes.find { it.id == noteId }
                    selectedNote?.let { NoteDetailScreen(it) }
                }
            }
        }
    }
}
