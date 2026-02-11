package com.slotmachine.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slotmachine.ui.theme.CreditGreen
import com.slotmachine.ui.theme.DarkChrome
import com.slotmachine.ui.theme.Gold
import com.slotmachine.ui.theme.LedRed
import com.slotmachine.ui.theme.SlotDarkPurple

@Composable
fun CreditMeter(
    credits: Int,
    bet: Int,
    lastWin: Int,
    modifier: Modifier = Modifier
) {
    val winColor by animateColorAsState(
        targetValue = if (lastWin > 0) Gold else DarkChrome,
        animationSpec = tween(500),
        label = "winColor"
    )

    val winAlpha = if (lastWin > 0) {
        val transition = rememberInfiniteTransition(label = "winPulse")
        val alpha by transition.animateFloat(
            initialValue = 0.6f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(400),
                repeatMode = RepeatMode.Reverse
            ),
            label = "winAlpha"
        )
        alpha
    } else {
        1f
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = SlotDarkPurple,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 2.dp,
                color = DarkChrome,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LedDisplay(label = "CREDITS", value = credits.toString(), color = CreditGreen)
        LedDisplay(
            label = "WIN",
            value = lastWin.toString(),
            color = winColor,
            modifier = Modifier.alpha(winAlpha)
        )
        LedDisplay(label = "BET", value = bet.toString(), color = LedRed)
    }
}

@Composable
private fun LedDisplay(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = label,
            color = DarkChrome,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 2.sp
        )
        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFF0A0A15),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = value,
                color = color,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
