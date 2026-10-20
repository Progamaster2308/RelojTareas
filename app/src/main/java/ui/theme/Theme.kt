package com.timetask.watch.ui.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

val LuxuryColors = Colors(
    primary = ChampagneGold,
    primaryVariant = ChampagneGoldMuted,
    secondary = Ivory,
    secondaryVariant = SecondaryGray,
    background = ObsidianBlack,
    surface = Graphite,
    error = DiscreetRed,
    onPrimary = ObsidianBlack,
    onSecondary = ObsidianBlack,
    onBackground = Ivory,
    onSurface = Ivory,
    onError = Ivory
)

@Composable
fun TimeTaskTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = LuxuryColors,
        typography = LuxuryTypography,
        content = content
    )
}