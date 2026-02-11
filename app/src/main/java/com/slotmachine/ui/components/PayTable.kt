package com.slotmachine.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slotmachine.model.Symbol
import com.slotmachine.ui.theme.Chrome
import com.slotmachine.ui.theme.DarkChrome
import com.slotmachine.ui.theme.Gold
import com.slotmachine.ui.theme.SlotDarkPurple

@Composable
fun PayTable(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = SlotDarkPurple,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = DarkChrome,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { expanded = !expanded }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (expanded) "PAY TABLE (tap to close)" else "PAY TABLE (tap to open)",
            color = Gold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        if (expanded) {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("SYMBOL", color = DarkChrome, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("x3", color = DarkChrome, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("x2", color = DarkChrome, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Symbol.entries.reversed().forEach { symbol ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${symbol.displayChar} ${symbol.displayName}",
                            color = Chrome,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${symbol.payout3x}x",
                            color = Gold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${symbol.payout2x}x",
                            color = Chrome,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Text(
                    text = "5 PAY LINES: Top, Middle, Bottom, Diag ↘, Diag ↗",
                    color = DarkChrome,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}
