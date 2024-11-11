@file:Suppress("DEPRECATION")

package com.example.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notes.ui.theme.NotesTheme

data class NotesAppItem(
    val id: Int,
    var title: String,
    var subtitle: String,
    val check: MutableState<Boolean> = mutableStateOf(false)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesTheme {
                NotesApp()
            }
        }
    }
}

@Composable
fun NotesApp() {
    val navController = rememberNavController()
    val noteList = remember { mutableStateListOf<NotesAppItem>() }

    NavHost(navController = navController, startDestination = "noteList") {
        composable("noteList") { NotesAppScreen(navController, noteList) }
        composable("addNote") { AddNoteScreen(navController, noteList) }
        composable("editNote/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")?.toIntOrNull()
            val noteItem = noteList.find { it.id == itemId }
            noteItem?.let { EditNoteScreen(navController, it) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesAppScreen(navController: NavController, noteList: MutableList<NotesAppItem>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes App") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addNote") }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Note")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(noteList) { note ->
                ListItem(
                    leadingContent = {
                        Checkbox(
                            checked = note.check.value,
                            onCheckedChange = {
                                note.check.value = !note.check.value
                            })},
                    headlineContent = { Text(note.title) },
                    supportingContent = { Text(note.subtitle)},
                    trailingContent = {
                        Row {
                            IconButton(
                                onClick = { navController.navigate("editNote/${note.id}") }
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit Note")
                            }
                            IconButton(
                                onClick = { noteList.remove(note) }
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete Note")
                            }
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(navController: NavController, noteList: MutableList<NotesAppItem>) {
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Note") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (title.isNotBlank() && subtitle.isNotBlank()) {
                                noteList.add(NotesAppItem(id = noteList.size, title = title, subtitle = subtitle))
                                navController.popBackStack()
                            }
                        }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = subtitle,
                onValueChange = { subtitle = it },
                label = { Text("Details") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (title.length >= 3 && title.length <= 50 && subtitle.isNotBlank() && subtitle.length <= 120) {
                    noteList.add(NotesAppItem(id = noteList.size, title = title, subtitle = subtitle))
                    navController.popBackStack()
                }
            }) {
                Text("Add Note")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(navController: NavController, note: NotesAppItem) {
    var title by remember { mutableStateOf(note.title) }
    var subtitle by remember { mutableStateOf(note.subtitle) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Note") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = subtitle,
                onValueChange = { subtitle = it },
                label = { Text("Details") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (title.length >= 3 && title.length <= 50 && subtitle.isNotBlank() && subtitle.length <= 120) {
                    note.title = title
                    note.subtitle = subtitle
                    navController.popBackStack()
                }
            }) {
                Text("Save Note")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NotesTheme {
        NotesApp()
    }
}