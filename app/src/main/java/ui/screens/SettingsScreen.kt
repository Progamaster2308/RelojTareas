package com.timetask.watch.ui.screens

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.timetask.watch.ui.components.LuxuryCard
import com.timetask.watch.ui.theme.*

@Composable
fun SettingsScreen(
    notificationsEnabled: Boolean,
    onToggleNotifications: (Boolean) -> Unit
) {
    val scalingLazyListState = rememberScalingLazyListState()
    val context = LocalContext.current
    val alarmManager = remember { context.getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    val canExactAlarm = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

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
                    text = "CONFIGURACIÓN",
                    style = LuxuryTypography.caption1,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            item {
                ToggleChip(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    checked = notificationsEnabled,
                    onCheckedChange = onToggleNotifications,
                    label = { Text("Notificaciones", style = LuxuryTypography.body2) },
                    secondaryLabel = {
                        Text(
                            text = if (notificationsEnabled) "Alertas activas" else "Silenciado",
                            style = LuxuryTypography.caption1.copy(
                                color = if (notificationsEnabled) ChampagneGold else SecondaryGray
                            )
                        )
                    },
                    toggleControl = {
                        Switch(
                            checked = notificationsEnabled,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ChampagneGold,
                                checkedTrackColor = SecondarySurface
                            )
                        )
                    }
                )
            }

            item {
                LuxuryCard(
                    onClick = {},
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "ESTADO DEL SISTEMA",
                        style = LuxuryTypography.caption1.copy(color = SecondaryGray)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (canExactAlarm) "Alarmas de alta precisión: ACTIVAS" else "Alarmas: MODO BATERÍA (aprox.)",
                        style = LuxuryTypography.body2.copy(
                            color = if (canExactAlarm) MutedGreen else DiscreetRed
                        )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "TimeTask Watch v1.0.0",
                        style = LuxuryTypography.body2.copy(color = ChampagneGold),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Arquitectura Nativa Wear OS",
                        style = LuxuryTypography.caption1.copy(color = SecondaryGray),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}