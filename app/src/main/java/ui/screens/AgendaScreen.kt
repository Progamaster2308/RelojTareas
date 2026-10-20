package com.timetask.watch.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.timetask.watch.data.model.Task
import com.timetask.watch.ui.components.LuxuryCard
import com.timetask.watch.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AgendaScreen(
    tasks: List<Task>,
    onToggleTask: (Task) -> Unit,
    onSelectTask: (Long) -> Unit
) {
    val scalingLazyListState = rememberScalingLazyListState()
    val localeMx = remember { Locale("es", "MX") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm", localeMx) }
    val hourHeaderFormatter = remember { DateTimeFormatter.ofPattern("HH:00 'hrs'", localeMx) }
    val now = LocalDateTime.now()

    Scaffold(
        timeText = { TimeText(timeTextStyle = LuxuryTypography.caption1) },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) }
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scalingLazyListState,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = 28.dp, start = 10.dp, end = 10.dp, bottom = 32.dp)
        ) {
            item {
                Text(
                    text = "AGENDA",
                    style = LuxuryTypography.caption1,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            if (tasks.isEmpty()) {
                item {
                    Text(
                        text = "No hay registros para hoy.",
                        style = LuxuryTypography.body1,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }
            } else {
                // Ordenar tareas cronológicamente
                val sortedTasks = tasks.sortedBy { it.scheduledTime }

                sortedTasks.forEachIndexed { index, task ->
                    val currentHour = task.scheduledTime.withMinute(0).withSecond(0).withNano(0)
                    val previousHour = if (index > 0) {
                        sortedTasks[index - 1].scheduledTime.withMinute(0).withSecond(0).withNano(0)
                    } else null

                    // Insertar encabezado por hora cuando cambia el bloque horario
                    if (previousHour == null || currentHour != previousHour) {
                        item(key = "header_${currentHour}") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp, bottom = 4.dp, start = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentHour.format(hourHeaderFormatter),
                                    style = LuxuryTypography.caption1.copy(color = SecondaryGray)
                                )
                            }
                        }
                    }

                    // Elemento de Tarea
                    item(key = task.id) {
                        val isPast = task.scheduledTime.isBefore(now) && !task.isCompleted
                        val borderColor = when {
                            task.isCompleted -> SecondarySurface.copy(alpha = 0.5f)
                            isPast -> DiscreetRed
                            else -> CardBorder
                        }

                        val textColor = when {
                            task.isCompleted -> SecondaryGray
                            else -> Ivory
                        }

                        val timeColor = when {
                            task.isCompleted -> SecondaryGray.copy(alpha = 0.7f)
                            isPast -> DiscreetRed
                            else -> ChampagneGold
                        }

                        val priorityColor = when {
                            task.isCompleted -> SecondaryGray.copy(alpha = 0.6f)
                            else -> Color(task.priority.colorHex)
                        }

                        LuxuryCard(
                            onClick = { onSelectTask(task.id) },
                            borderColor = borderColor,
                            modifier = Modifier.padding(vertical = 5.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${task.scheduledTime.format(timeFormatter)} hrs",
                                    style = LuxuryTypography.caption1.copy(color = timeColor)
                                )
                                ToggleButton(
                                    checked = task.isCompleted,
                                    onCheckedChange = { onToggleTask(task) },
                                    colors = ToggleButtonDefaults.toggleButtonColors(
                                        checkedBackgroundColor = MutedGreen,
                                        checkedContentColor = ObsidianBlack,
                                        uncheckedBackgroundColor = SecondarySurface,
                                        uncheckedContentColor = Ivory
                                    ),
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    if (task.isCompleted) {
                                        Text("✓", style = LuxuryTypography.caption1)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = task.title,
                                style = LuxuryTypography.title2.copy(
                                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                                    color = textColor
                                )
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "${task.category.displayName} • ${task.priority.displayName}",
                                style = LuxuryTypography.body2.copy(
                                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                                    color = priorityColor
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}