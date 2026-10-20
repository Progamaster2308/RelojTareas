package com.timetask.watch

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.timetask.watch.scheduling.AlarmScheduler
import com.timetask.watch.ui.screens.*
import com.timetask.watch.ui.theme.TimeTaskTheme
import com.timetask.watch.viewmodel.TaskViewModel
import com.timetask.watch.viewmodel.TaskViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: TaskViewModel

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as TimeTaskApplication
        val scheduler = AlarmScheduler(this)
        val factory = TaskViewModelFactory(app.repository, scheduler)
        viewModel = ViewModelProvider(this, factory)[TaskViewModel::class.java]

        checkAndRequestPermissions()

        setContent {
            // SOLUCIÓN AL CRASH: Proveer el LifecycleOwner explícitamente
            CompositionLocalProvider(LocalLifecycleOwner provides this) {
                TimeTaskTheme {
                    val navController = rememberSwipeDismissableNavController()

                    // Ahora estos recolectores funcionarán sin lanzar IllegalStateException
                    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                    val nextTask = viewModel.nextTask.collectAsStateWithLifecycle().value
                    val allTasks = viewModel.allTasks.collectAsStateWithLifecycle().value
                    val notifEnabled = app.preferencesRepository.notificationsEnabled
                        .collectAsStateWithLifecycle(initialValue = true).value

                    SwipeDismissableNavHost(
                        navController = navController,
                        startDestination = "today"
                    ) {
                        composable("today") {
                            TodayScreen(
                                uiState = uiState,
                                nextTask = nextTask,
                                onNavigateToAgenda = { navController.navigate("agenda") },
                                onNavigateToCreate = { navController.navigate("create") },
                                onNavigateToStats = { navController.navigate("stats") },
                                onNavigateToSettings = { navController.navigate("settings") },
                                onSelectTask = { taskId -> navController.navigate("detail/$taskId") }
                            )
                        }

                        composable("agenda") {
                            AgendaScreen(
                                tasks = uiState.todayTasks,
                                onToggleTask = { viewModel.toggleTaskCompletion(it) },
                                onSelectTask = { taskId -> navController.navigate("detail/$taskId") }
                            )
                        }

                        composable("create") {
                            TaskFormScreen(
                                initialTask = null,
                                onSave = { title, desc, time, cat, prio, rep, notif ->
                                    viewModel.createTask(title, desc, time, cat, prio, rep, notif)
                                    navController.popBackStack()
                                },
                                onCancel = { navController.popBackStack() }
                            )
                        }

                        composable("detail/{taskId}") { backStackEntry ->
                            val taskId = backStackEntry.arguments?.getString("taskId")?.toLongOrNull() ?: -1L
                            val task = allTasks.find { it.id == taskId }

                            TaskDetailScreen(
                                task = task,
                                onToggleCompletion = { viewModel.toggleTaskCompletion(it) },
                                onSnooze = { t, mins -> viewModel.snoozeTask(t, mins) },
                                onDelete = { viewModel.deleteTask(it) },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("stats") {
                            StatsScreen(tasks = uiState.todayTasks)
                        }

                        composable("settings") {
                            SettingsScreen(
                                notificationsEnabled = notifEnabled,
                                onToggleNotifications = { enabled ->
                                    lifecycleScope.launch {
                                        app.preferencesRepository.setNotificationsEnabled(enabled)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}