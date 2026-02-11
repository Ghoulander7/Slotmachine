package com.slotmachine.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slotmachine.model.Symbol
import com.slotmachine.ui.theme.DarkChrome
import com.slotmachine.ui.theme.Gold
import com.slotmachine.ui.theme.ReelBackground
import kotlinx.coroutines.delay

@Composable
fun ReelView(
    symbols: List<Symbol>,
    isSpinning: Boolean,
    spinSequence: List<Symbol>,
    isWinning: Boolean,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(8.dp)
    val borderColor = if (isWinning) Gold else DarkChrome

    // Spinning animation state
    var displaySymbols by remember { mutableIntStateOf(0) }
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(isSpinning) {
        if (isSpinning && spinSequence.isNotEmpty()) {
            // Cycle through the spin sequence symbols rapidly
            val totalSteps = spinSequence.size - 2 // Last 3 symbols are the final window
            for (i in 0 until totalSteps) {
                displaySymbols = i
                val delayMs = 40L + (i * 3L) // Gradually slow down
                delay(delayMs)
            }
            // Snap to final position
            displaySymbols = totalSteps
        }
    }

    val currentSymbols = if (isSpinning && spinSequence.size >= 3) {
        val idx = displaySymbols.coerceAtMost(spinSequence.size - 3)
        spinSequence.subList(idx, idx + 3)
    } else {
        symbols
    }

    Box(
        modifier = modifier
            .width(100.dp)
            .clip(shape)
            .background(ReelBackground, shape)
            .border(2.dp, borderColor, shape)
            .drawWithContent {
                drawContent()
                // Top fade gradient
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xCC1A1A2E),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = size.height * 0.12f
                    )
                )
                // Bottom fade gradient
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xCC1A1A2E)
                        ),
                        startY = size.height * 0.88f,
                        endY = size.height
                    )
                )
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            currentSymbols.forEachIndexed { index, symbol ->
                Box(
                    modifier = Modifier
                        .height(72.dp)
                        .width(100.dp)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = symbol.displayChar,
                        fontSize = 40.sp,
                        textAlign = TextAlign.Center
                    )
                }
                if (index < currentSymbols.lastIndex) {
                    HorizontalDivider(
                        color = DarkChrome.copy(alpha = 0.3f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}
