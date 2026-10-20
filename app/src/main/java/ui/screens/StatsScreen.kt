package com.timetask.watch.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.timetask.watch.data.model.Task
import com.timetask.watch.ui.components.LuxuryCard
import com.timetask.watch.ui.components.LuxuryCircularProgress
import com.timetask.watch.ui.theme.*
import java.time.LocalDateTime

@Composable
fun StatsScreen(
    tasks: List<Task>
) {
    val scalingLazyListState = rememberScalingLazyListState()
    val now = LocalDateTime.now()

    val total = tasks.size
    val completed = tasks.count { it.isCompleted }
    val overdue = tasks.count { !it.isCompleted && it.scheduledTime.isBefore(now) }
    val pending = total - completed
    val ratio = if (total > 0) completed.toFloat() / total else 0f

    Scaffold(
        timeText = { TimeText(timeTextStyle = LuxuryTypography.caption1) },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) }
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scalingLazyListState,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = 28.dp, start = 12.dp, end = 12.dp, bottom = 28.dp)
        ) {
            item {
                Text(
                    text = "MÉTRICAS DEL DÍA",
                    style = LuxuryTypography.caption1,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Anillo de Progreso (Un solo texto central controlado por LuxuryCircularProgress)
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    LuxuryCircularProgress(
                        progress = ratio,
                        size = 85.dp,
                        strokeWidth = 4.dp
                    )
                }
            }

            // Desglose Numérico de Tareas
            item {
                LuxuryCard(
                    onClick = {},
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Total programado", style = LuxuryTypography.body2)
                        Text(text = "$total", style = LuxuryTypography.title2)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Completadas", style = LuxuryTypography.body2)
                        Text(text = "$completed", style = LuxuryTypography.title2.copy(color = MutedGreen))
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Pendientes", style = LuxuryTypography.body2)
                        Text(text = "$pending", style = LuxuryTypography.title2.copy(color = ChampagneGold))
                    }

                    if (overdue > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Vencidas", style = LuxuryTypography.body2)
                            Text(text = "$overdue", style = LuxuryTypography.title2.copy(color = DiscreetRed))
                        }
                    }
                }
            }
        }
    }
}