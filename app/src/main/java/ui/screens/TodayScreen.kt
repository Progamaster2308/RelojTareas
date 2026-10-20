package com.timetask.watch.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.timetask.watch.data.model.Task
import com.timetask.watch.ui.components.LuxuryButton
import com.timetask.watch.ui.components.LuxuryCard
import com.timetask.watch.ui.components.LuxuryCircularProgress
import com.timetask.watch.ui.theme.*
import com.timetask.watch.viewmodel.TaskUiState
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TodayScreen(
    uiState: TaskUiState,
    nextTask: Task?,
    onNavigateToAgenda: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onSelectTask: (Long) -> Unit
) {
    val scalingLazyListState = rememberScalingLazyListState()
    val localeMx = remember { Locale("es", "MX") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm", localeMx) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", localeMx) }
    val now = LocalDateTime.now()

    Scaffold(
        timeText = { TimeText(timeTextStyle = LuxuryTypography.caption1) },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) }
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scalingLazyListState,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = 28.dp, start = 12.dp, end = 12.dp, bottom = 36.dp)
        ) {
            // Encabezado de Marca
            item {
                Text(
                    text = "TIMETASK",
                    style = LuxuryTypography.caption1,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Reloj Digital
            item {
                Text(
                    text = now.format(timeFormatter),
                    style = LuxuryTypography.title1,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            // Fecha
            item {
                Text(
                    text = now.format(dateFormatter).replaceFirstChar { it.uppercase() },
                    style = LuxuryTypography.body2,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Anillo de Progreso y Balance Diario
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    LuxuryCircularProgress(
                        progress = uiState.completionRatio,
                        size = 100.dp,
                        strokeWidth = 4.dp
                    )
                }
            }

            item {
                Text(
                    text = "${uiState.completedCount} completadas  •  ${uiState.pendingCount} pendientes",
                    style = LuxuryTypography.body2.copy(color = SecondaryGray),
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

            // Próximo Evento o Estado Vacío
            item {
                if (nextTask != null) {
                    val minutesRemaining = Duration.between(LocalDateTime.now(), nextTask.scheduledTime).toMinutes()
                    val remainingText = if (minutesRemaining > 0) "en ${minutesRemaining}m" else "ahora"

                    LuxuryCard(
                        onClick = { onSelectTask(nextTask.id) },
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "SIGUIENTE", style = LuxuryTypography.caption1)
                            Text(text = remainingText, style = LuxuryTypography.caption1)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = nextTask.title, style = LuxuryTypography.title2)
                        Text(
                            text = "${nextTask.scheduledTime.format(timeFormatter)} hrs  •  ${nextTask.category.displayName}",
                            style = LuxuryTypography.body2
                        )
                    }
                } else {
                    LuxuryCard(
                        onClick = onNavigateToCreate,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "Día despejado",
                                style = LuxuryTypography.title2,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Toca para organizar tu primera actividad",
                                style = LuxuryTypography.body2.copy(color = SecondaryGray),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Acciones Rápidas
            item {
                Spacer(modifier = Modifier.height(6.dp))
                LuxuryButton(
                    text = "+ Nueva Tarea",
                    onClick = onNavigateToCreate,
                    isPrimary = true
                )
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                LuxuryButton(
                    text = "Agenda Completa",
                    onClick = onNavigateToAgenda,
                    isPrimary = false
                )
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CompactChip(
                        onClick = onNavigateToStats,
                        label = { Text("Métricas", style = LuxuryTypography.body2) },
                        colors = ChipDefaults.secondaryChipColors()
                    )
                    CompactChip(
                        onClick = onNavigateToSettings,
                        label = { Text("Ajustes", style = LuxuryTypography.body2) },
                        colors = ChipDefaults.secondaryChipColors()
                    )
                }
            }
        }
    }
}