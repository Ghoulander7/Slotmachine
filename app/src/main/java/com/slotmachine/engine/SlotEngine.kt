package com.slotmachine.engine

import com.slotmachine.model.PayLine
import com.slotmachine.model.Symbol
import com.slotmachine.model.WinResult
import kotlin.random.Random

/**
 * Core slot machine engine handling reel strip generation, spinning, and payout calculation.
 *
 * Each reel has a virtual strip of symbols built from a weighted pool.
 * A spin selects a random stop position on each strip and displays
 * a window of 3 consecutive symbols.
 */
class SlotEngine(private val random: Random = Random.Default) {

    private val reelStrips: List<List<Symbol>> = List(3) { generateReelStrip() }

    private fun generateReelStrip(): List<Symbol> {
        val pool = Symbol.weightedPool()
        return List(pool.size) { pool[random.nextInt(pool.size)] }
    }

    /** Spin all 3 reels and return the 3x3 visible window (each reel shows 3 rows). */
    fun spin(): List<List<Symbol>> {
        return reelStrips.map { strip ->
            val stopPos = random.nextInt(strip.size)
            List(3) { row ->
                strip[(stopPos + row) % strip.size]
            }
        }
    }

    /** Evaluate all pay lines and return any winning results. */
    fun evaluateWins(reelWindows: List<List<Symbol>>, bet: Int): List<WinResult> {
        val wins = mutableListOf<WinResult>()

        for (payLine in PayLine.entries) {
            val symbols = payLine.getSymbols(reelWindows)

            // Check for 3-of-a-kind only
            if (symbols[0] == symbols[1] && symbols[1] == symbols[2]) {
                val symbol = symbols[0]
                val payout = symbol.payout3x * bet
                wins.add(WinResult(payLine, symbol, 3, payout))
            }
        }

        return wins
    }

    /** Get the stop positions for animation (returns longer strips for visual scrolling). */
    fun getSpinSequence(finalWindow: List<Symbol>, extraSymbols: Int = 12): List<Symbol> {
        val pool = Symbol.weightedPool()
        val prefix = List(extraSymbols) { pool[random.nextInt(pool.size)] }
        return prefix + finalWindow
    }
}
