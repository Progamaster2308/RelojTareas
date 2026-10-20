package com.timetask.watch.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.timetask.watch.data.model.Category
import com.timetask.watch.data.model.Priority
import com.timetask.watch.data.model.RepeatMode
import com.timetask.watch.data.model.Task
import com.timetask.watch.ui.components.LuxuryButton
import com.timetask.watch.ui.theme.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun TaskFormScreen(
    initialTask: Task? = null,
    onSave: (title: String, desc: String, time: LocalDateTime, cat: Category, prio: Priority, rep: RepeatMode, notif: Boolean) -> Unit,
    onCancel: () -> Unit
) {
    val scalingLazyListState = rememberScalingLazyListState()

    // Actividades ampliadas y útiles para desarrollo, vida diaria, estudio y salud
    val presetTitles = listOf(
        "Reunión",
        "Desarrollo / Código",
        "Entrenamiento",
        "Estudio",
        "Examen / Entrega",
        "Medicamento",
        "Llamada clave",
        "Lectura",
        "Revisión"
    )

    var selectedTitleIndex by remember { mutableIntStateOf(0) }
    var hour by remember { mutableIntStateOf(initialTask?.scheduledTime?.hour ?: LocalTime.now().plusHours(1).hour) }
    var minute by remember {
        val rawMinute = initialTask?.scheduledTime?.minute ?: LocalTime.now().minute
        mutableIntStateOf((rawMinute / 5) * 5)
    }

    var category by remember { mutableStateOf(initialTask?.category ?: Category.PERSONAL) }
    var priority by remember { mutableStateOf(initialTask?.priority ?: Priority.NORMAL) }
    var repeatMode by remember { mutableStateOf(initialTask?.repeatMode ?: RepeatMode.NONE) }
    var notify by remember { mutableStateOf(initialTask?.notifyUser ?: true) }

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
                    text = if (initialTask == null) "NUEVA TAREA" else "EDITAR TAREA",
                    style = LuxuryTypography.caption1,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            // Selector Rotativo de Actividad Ampliado
            item {
                Chip(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    onClick = {
                        selectedTitleIndex = (selectedTitleIndex + 1) % presetTitles.size
                    },
                    label = { Text("Actividad", style = LuxuryTypography.body2) },
                    secondaryLabel = {
                        Text(
                            text = presetTitles[selectedTitleIndex],
                            color = ChampagneGold,
                            style = LuxuryTypography.body1,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = ChipDefaults.secondaryChipColors()
                )
            }

            // Selector de Horario con minutos precisos
            item {
                Card(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    backgroundPainter = CardDefaults.cardBackgroundPainter(
                        startBackgroundColor = SecondarySurface,
                        endBackgroundColor = SecondarySurface
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "HORARIO",
                            style = LuxuryTypography.caption1.copy(color = SecondaryGray),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CompactChip(
                                onClick = { hour = (hour + 23) % 24 },
                                label = { Text("-1h", style = LuxuryTypography.caption1) },
                                colors = ChipDefaults.secondaryChipColors()
                            )

                            Text(
                                text = String.format("%02d:%02d", hour, minute),
                                style = LuxuryTypography.title1.copy(color = ChampagneGold)
                            )

                            CompactChip(
                                onClick = { hour = (hour + 1) % 24 },
                                label = { Text("+1h", style = LuxuryTypography.caption1) },
                                colors = ChipDefaults.secondaryChipColors()
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CompactChip(
                                onClick = { minute = (minute + 55) % 60 },
                                label = { Text("-5m", style = LuxuryTypography.caption1) },
                                colors = ChipDefaults.secondaryChipColors()
                            )
                            CompactChip(
                                onClick = { minute = (minute + 59) % 60 },
                                label = { Text("-1m", style = LuxuryTypography.caption1) },
                                colors = ChipDefaults.secondaryChipColors()
                            )
                            CompactChip(
                                onClick = { minute = (minute + 1) % 60 },
                                label = { Text("+1m", style = LuxuryTypography.caption1) },
                                colors = ChipDefaults.secondaryChipColors()
                            )
                            CompactChip(
                                onClick = { minute = (minute + 5) % 60 },
                                label = { Text("+5m", style = LuxuryTypography.caption1) },
                                colors = ChipDefaults.secondaryChipColors()
                            )
                        }
                    }
                }
            }

            // Chips de Categoría (Permite rotar o mostrar las principales disponibles)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CATEGORÍA",
                        style = LuxuryTypography.caption1.copy(color = SecondaryGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, bottom = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Category.entries.take(3).forEach { cat ->
                            val isSelected = category == cat
                            CompactChip(
                                onClick = { category = cat },
                                label = {
                                    Text(
                                        text = cat.displayName,
                                        style = LuxuryTypography.caption1,
                                        color = if (isSelected) ObsidianBlack else Ivory,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                colors = if (isSelected) {
                                    ChipDefaults.primaryChipColors(
                                        backgroundColor = ChampagneGold,
                                        contentColor = ObsidianBlack
                                    )
                                } else {
                                    ChipDefaults.secondaryChipColors()
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Prioridad
            item {
                Chip(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    onClick = {
                        val nextOrdinal = (priority.ordinal + 1) % Priority.entries.size
                        priority = Priority.entries[nextOrdinal]
                    },
                    label = { Text("Prioridad", style = LuxuryTypography.body2) },
                    secondaryLabel = { Text(priority.displayName, color = Color(priority.colorHex), style = LuxuryTypography.body2) },
                    colors = ChipDefaults.secondaryChipColors()
                )
            }

            // Recurrencia
            item {
                Chip(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    onClick = {
                        val nextOrdinal = (repeatMode.ordinal + 1) % RepeatMode.entries.size
                        repeatMode = RepeatMode.entries[nextOrdinal]
                    },
                    label = { Text("Repetir", style = LuxuryTypography.body2) },
                    secondaryLabel = { Text(repeatMode.displayName, color = Ivory, style = LuxuryTypography.body2) },
                    colors = ChipDefaults.secondaryChipColors()
                )
            }

            // Notificación Toggle
            item {
                ToggleChip(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    checked = notify,
                    onCheckedChange = { notify = it },
                    label = { Text("Alarma", style = LuxuryTypography.body2) },
                    toggleControl = {
                        Switch(
                            checked = notify,
                            colors = SwitchDefaults.colors(checkedThumbColor = ChampagneGold)
                        )
                    }
                )
            }

            // Botones de Acción
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        LuxuryButton(
                            text = "Cancelar",
                            onClick = onCancel,
                            isPrimary = false
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        LuxuryButton(
                            text = "Guardar",
                            onClick = {
                                val targetTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minute))
                                val finalDateTime = if (targetTime.isBefore(LocalDateTime.now())) {
                                    targetTime.plusDays(1)
                                } else targetTime

                                onSave(
                                    presetTitles[selectedTitleIndex],
                                    "",
                                    finalDateTime,
                                    category,
                                    priority,
                                    repeatMode,
                                    notify
                                )
                            },
                            isPrimary = true
                        )
                    }
                }
            }
        }
    }
}