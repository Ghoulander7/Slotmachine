package com.slotmachine.ui

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slotmachine.engine.SlotViewModel
import com.slotmachine.ui.components.CreditMeter
import com.slotmachine.ui.components.PayTable
import com.slotmachine.ui.components.ReelView
import com.slotmachine.ui.components.WinDisplay
import com.slotmachine.ui.theme.Chrome
import com.slotmachine.ui.theme.CrimsonRed
import com.slotmachine.ui.theme.DarkChrome
import com.slotmachine.ui.theme.DarkGold
import com.slotmachine.ui.theme.Gold
import com.slotmachine.ui.theme.SlotBackground
import com.slotmachine.ui.theme.SlotDarkPurple

@Composable
fun SlotMachineScreen(viewModel: SlotViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()
    val spinningReels by viewModel.spinningReels.collectAsState()
    val reelSequences by viewModel.reelSequences.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(SlotBackground, SlotDarkPurple, SlotBackground)
                )
            )
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(DarkGold, Gold, DarkGold)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "LUCKY SEVENS",
                color = SlotBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp,
                fontFamily = FontFamily.SansSerif
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Pay Table (collapsible)
        PayTable()

        Spacer(modifier = Modifier.height(12.dp))

        // Message Display
        val messageColor by animateColorAsState(
            targetValue = if (state.lastWinTotal > 0) Gold else Chrome,
            animationSpec = tween(300),
            label = "messageColor"
        )
        Text(
            text = state.message,
            color = messageColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // === REEL DISPLAY ===
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .background(
                    color = Color(0xFF0D0D1A),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 3.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(Gold.copy(alpha = 0.6f), DarkChrome, Gold.copy(alpha = 0.6f))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Pay line indicators on left side
            Column(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                PayLineDot(color = Color.Red, label = "T")
                Spacer(modifier = Modifier.height(44.dp))
                PayLineDot(color = Color.Green, label = "M")
                Spacer(modifier = Modifier.height(44.dp))
                PayLineDot(color = Color.Blue, label = "B")
            }

            // The 3 reels
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                for (i in 0 until 3) {
                    val winningReel = state.wins.any { win ->
                        val symbols = win.payLine.getSymbols(state.reelWindows)
                        symbols[i] == win.symbol
                    }
                    ReelView(
                        symbols = state.reelWindows[i],
                        isSpinning = spinningReels[i],
                        spinSequence = if (i < reelSequences.size) reelSequences[i] else emptyList(),
                        isWinning = winningReel && !state.isSpinning,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Pay line indicators on right side
            Column(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                PayLineDot(color = Color.Yellow, label = "↘")
                Spacer(modifier = Modifier.height(44.dp))
                PayLineDot(color = Color.Transparent, label = "")
                Spacer(modifier = Modifier.height(44.dp))
                PayLineDot(color = Color.Cyan, label = "↗")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Win Display
        WinDisplay(wins = state.wins)

        Spacer(modifier = Modifier.height(12.dp))

        // Credit Meter
        CreditMeter(
            credits = state.credits,
            bet = state.bet,
            lastWin = state.lastWinTotal
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bet Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BetButton(
                text = "-",
                enabled = state.canDecreaseBet,
                onClick = { viewModel.decreaseBet() }
            )

            TextButton(
                onClick = { viewModel.maxBet() },
                enabled = !state.isSpinning
            ) {
                Text(
                    text = "MAX BET",
                    color = if (!state.isSpinning) Gold else DarkChrome,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            BetButton(
                text = "+",
                enabled = state.canIncreaseBet,
                onClick = { viewModel.increaseBet() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // SPIN Button
        val spinScale = if (state.isSpinning) {
            val transition = rememberInfiniteTransition(label = "spinPulse")
            val scale by transition.animateFloat(
                initialValue = 0.95f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(300),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "spinScale"
            )
            scale
        } else {
            1f
        }

        Button(
            onClick = { viewModel.spin() },
            enabled = state.canSpin,
            modifier = Modifier
                .size(width = 200.dp, height = 64.dp)
                .scale(spinScale)
                .shadow(
                    elevation = if (state.canSpin) 12.dp else 4.dp,
                    shape = RoundedCornerShape(32.dp)
                ),
            shape = RoundedCornerShape(32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CrimsonRed,
                disabledContainerColor = DarkChrome
            )
        ) {
            Text(
                text = if (state.isSpinning) "SPINNING..." else "SPIN",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (state.canSpin) Color.White else Color.Gray,
                letterSpacing = 3.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PayLineDot(color: Color, label: String) {
    Box(
        modifier = Modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 8.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BetButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(48.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Gold.copy(alpha = 0.8f),
            disabledContainerColor = DarkChrome.copy(alpha = 0.5f)
        )
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = SlotBackground
        )
    }
}
