package com.slotmachine.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SlotColorScheme = darkColorScheme(
    primary = Gold,
    secondary = CrimsonRed,
    tertiary = CasinoGreen,
    background = SlotBackground,
    surface = SlotDarkPurple,
    onPrimary = SlotBackground,
    onSecondary = Chrome,
    onBackground = Chrome,
    onSurface = Chrome,
)

@Composable
fun SlotMachineTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SlotColorScheme,
        typography = SlotTypography,
        content = content
    )
}
