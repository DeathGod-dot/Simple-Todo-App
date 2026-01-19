package com.example.goooapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost

import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.goooapp.screenui.EditTaskScreen
import com.example.goooapp.screenui.TodoScreen
import com.example.goooapp.viewmodel.TodoViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            val viewModel: TodoViewModel = run {
                val app = LocalContext.current.applicationContext as android.app.Application
                viewModel(factory = ViewModelProvider.AndroidViewModelFactory.getInstance(app))
            }

            NavHost(
                navController = navController,
                startDestination = "list"
            ) {

                composable("list") {
                    TodoScreen(navController = navController, viewModel = viewModel)
                }

                composable(
                    "edit/{taskId}",
                    arguments = listOf(
                        navArgument("taskId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val taskId =
                        backStackEntry.arguments!!.getInt("taskId")

                    EditTaskScreen(
                        taskId = taskId,
                        viewModel = viewModel,
                        onSave = { navController.popBackStack() },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

