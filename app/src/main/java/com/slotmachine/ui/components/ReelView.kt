package com.slotmachine.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slotmachine.model.Symbol
import com.slotmachine.ui.theme.DarkChrome
import com.slotmachine.ui.theme.Gold
import com.slotmachine.ui.theme.ReelBackground
import kotlin.math.roundToInt

private val SYMBOL_HEIGHT = 72.dp
private val DIVIDER_HEIGHT = 1.dp
private val CELL_HEIGHT = SYMBOL_HEIGHT + DIVIDER_HEIGHT

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

    // Animate a float representing position in the spin sequence.
    // 0f = start of sequence, (spinSequence.size - 3).toFloat() = final window
    val animPosition = remember { Animatable(0f) }
    val cellHeightPx = with(LocalDensity.current) { CELL_HEIGHT.toPx() }

    LaunchedEffect(isSpinning) {
        if (isSpinning && spinSequence.size >= 4) {
            val targetIndex = (spinSequence.size - 3).toFloat()
            animPosition.snapTo(0f)
            animPosition.animateTo(
                targetValue = targetIndex,
                animationSpec = keyframes {
                    durationMillis = 2000
                    // Fast constant speed for first half
                    (targetIndex * 0.85f) at 1000 using LinearEasing
                    // Decelerate to final position
                    (targetIndex * 0.97f) at 1600 using FastOutSlowInEasing
                    targetIndex at 2000 using FastOutSlowInEasing
                }
            )
        }
    }

    // Derive which symbols to show and the sub-cell vertical offset
    val position = animPosition.value
    val baseIndex = position.toInt().coerceIn(0, (spinSequence.size - 4).coerceAtLeast(0))
    val fraction = position - baseIndex
    val yOffsetPx = -(fraction * cellHeightPx)

    // Show 4 symbols so we can scroll smoothly between them (1 extra for transition)
    val visibleSymbols = if (isSpinning && spinSequence.size >= 4) {
        val end = (baseIndex + 4).coerceAtMost(spinSequence.size)
        spinSequence.subList(baseIndex, end)
    } else {
        symbols
    }

    Box(
        modifier = modifier
            .width(100.dp)
            .height(SYMBOL_HEIGHT * 3 + DIVIDER_HEIGHT * 2)
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
        // Use a clipped box to hide overflow during scrolling
        Box(modifier = Modifier.clip(shape)) {
            visibleSymbols.forEachIndexed { index, symbol ->
                val slotYPx = index * cellHeightPx + yOffsetPx
                Box(
                    modifier = Modifier
                        .offset { IntOffset(0, slotYPx.roundToInt()) }
                        .height(SYMBOL_HEIGHT)
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
                // Draw divider below each symbol except the last visible
                if (index < visibleSymbols.lastIndex) {
                    val dividerYPx = (index + 1) * cellHeightPx + yOffsetPx - with(LocalDensity.current) { DIVIDER_HEIGHT.toPx() }
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(0, dividerYPx.roundToInt()) }
                            .padding(horizontal = 8.dp)
                    ) {
                        HorizontalDivider(
                            color = DarkChrome.copy(alpha = 0.3f),
                            thickness = DIVIDER_HEIGHT,
                            modifier = Modifier.width(84.dp)
                        )
                    }
                }
            }
        }
    }
}
