package com.example.goooapp.screenui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.goooapp.viewmodel.TodoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.goooapp.model.TaskFilter
import com.example.goooapp.model.TodoItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    navController: NavHostController? = null,
    viewModel: TodoViewModel = run {
        val app = LocalContext.current.applicationContext as android.app.Application
        viewModel(factory = ViewModelProvider.AndroidViewModelFactory.getInstance(app))
    }
) {
    var taskText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val tasks by viewModel.filteredTasks.collectAsState(initial = emptyList())

    var editingTaskId by remember { mutableStateOf<Int?>(null) }
    var editTitle by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val editingTask = tasks.firstOrNull { it.id == editingTaskId }

    LaunchedEffect(editingTaskId, editingTask?.title) {
        editTitle = editingTask?.title.orEmpty()
    }

    InteractiveAuroraBackground {

        if (editingTaskId != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    editingTaskId = null
                    editTitle = ""
                },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)
            ) {
                val currentTask = editingTask ?: return@ModalBottomSheet

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 18.dp)
                ) {
                    Text(
                        text = "Edit task",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(12.dp))

                    TextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Completed",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = currentTask.isDone,
                            onCheckedChange = { checked ->
                                viewModel.toggleTask(currentTask, checked)
                            }
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                editingTaskId = null
                                editTitle = ""
                            }
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            modifier = Modifier.weight(1f),
                            enabled = editTitle.isNotBlank(),
                            onClick = {
                                viewModel.updateTask(currentTask, editTitle)
                                editingTaskId = null
                                editTitle = ""
                            }
                        ) {
                            Text("Save")
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    TextButton(
                        onClick = {
                            viewModel.deleteTask(currentTask, tasks)
                            editingTaskId = null
                            editTitle = ""
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Delete")
                    }
                }
            }
        }

        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "My Todo App",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    actions = {
                        TextButton(onClick = {
                            viewModel.clearCompletedTasks()
                        }) {
                            Text("Clear Done")
                        }
                    }
                )
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Add a task",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = taskText,
                                onValueChange = { taskText = it },
                                placeholder = { Text("Enter Task") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
                                )
                            )
                            Spacer(Modifier.width(10.dp))
                            FilledIconButton(
                                onClick = {
                                    viewModel.addTask(taskText)
                                    taskText = ""
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add"
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterButton(
                        text = "All",
                        selected = viewModel.currentFilter == TaskFilter.ALL
                    ) {
                        viewModel.setFilter(TaskFilter.ALL)
                    }

                    FilterButton(
                        text = "Pending",
                        selected = viewModel.currentFilter == TaskFilter.PENDING
                    ) {
                        viewModel.setFilter(TaskFilter.PENDING)
                    }

                    FilterButton(
                        text = "Done",
                        selected = viewModel.currentFilter == TaskFilter.COMPLETED
                    ) {
                        viewModel.setFilter(TaskFilter.COMPLETED)
                    }
                }

                Spacer(Modifier.height(10.dp))

                if (tasks.isEmpty()) {
                    EmptyState()
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .animateContentSize(),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(
                            items = tasks,
                            key = { it.id }
                        ) { task ->
                            TodoItemRow(
                                modifier = Modifier.animateContentSize(),
                                task = task,
                                onCheckedChange = { checked ->
                                    viewModel.toggleTask(task, checked)
                                },
                                onDeleteClick = {
                                    viewModel.deleteTask(task, tasks)

                                    scope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "Task Deleted",
                                            actionLabel = "Undo",
                                            duration = SnackbarDuration.Short
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            viewModel.undoDelete()
                                        }
                                    }
                                },
                                onClick = {
                                    editingTaskId = task.id
                                }

                            )
                        }
                    }
                }

            }
        }
    }

}