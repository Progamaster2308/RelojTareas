package com.timetask.watch.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.timetask.watch.data.model.Task
import com.timetask.watch.ui.components.LuxuryButton
import com.timetask.watch.ui.theme.*
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TaskDetailScreen(
    task: Task?,
    onToggleCompletion: (Task) -> Unit,
    onSnooze: (Task, Int) -> Unit,
    onDelete: (Task) -> Unit,
    onBack: () -> Unit
) {
    val scalingLazyListState = rememberScalingLazyListState()
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm 'hrs' • d MMM", Locale("es", "MX")) }

    Scaffold(
        timeText = { TimeText(timeTextStyle = LuxuryTypography.caption1) },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) }
    ) {
        if (task == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Tarea no encontrada", style = LuxuryTypography.body1)
            }
            return@Scaffold
        }

        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scalingLazyListState,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = 28.dp, start = 10.dp, end = 10.dp, bottom = 32.dp)
        ) {
            // Categoría y Prioridad
            item {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 2.dp)
                ) {
                    Text(
                        text = task.category.displayName.uppercase(),
                        style = LuxuryTypography.caption1.copy(color = SecondaryGray)
                    )
                    Text(
                        text = "  •  ",
                        style = LuxuryTypography.caption1.copy(color = SecondaryGray)
                    )
                    Text(
                        text = task.priority.displayName.uppercase(),
                        style = LuxuryTypography.caption1.copy(color = Color(task.priority.colorHex))
                    )
                }
            }

            // Título Principal
            item {
                Text(
                    text = task.title,
                    style = LuxuryTypography.title1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }

            // Hora programada
            item {
                Text(
                    text = task.scheduledTime.format(timeFormatter),
                    style = LuxuryTypography.body2.copy(color = ChampagneGold),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            // Acción Principal: Completar / Reactivar
            item {
                LuxuryButton(
                    text = if (task.isCompleted) "Reactivar Tarea" else "Marcar Completada",
                    onClick = {
                        onToggleCompletion(task)
                        onBack()
                    },
                    isPrimary = !task.isCompleted
                )
            }

            // Bloque de Posponer
            if (!task.isCompleted) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "POSPONER",
                        style = LuxuryTypography.caption1.copy(color = SecondaryGray),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CompactChip(
                            onClick = { onSnooze(task, 5); onBack() },
                            label = { Text("+5m", style = LuxuryTypography.caption1) },
                            colors = ChipDefaults.secondaryChipColors()
                        )
                        CompactChip(
                            onClick = { onSnooze(task, 10); onBack() },
                            label = { Text("+10m", style = LuxuryTypography.caption1) },
                            colors = ChipDefaults.secondaryChipColors()
                        )
                        CompactChip(
                            onClick = { onSnooze(task, 15); onBack() },
                            label = { Text("+15m", style = LuxuryTypography.caption1) },
                            colors = ChipDefaults.secondaryChipColors()
                        )
                    }
                }
            }

            // Eliminar Registro
            item {
                Spacer(modifier = Modifier.height(10.dp))
                LuxuryButton(
                    text = "Eliminar Registro",
                    onClick = {
                        onDelete(task)
                        onBack()
                    },
                    isPrimary = false
                )
            }
        }
    }
}