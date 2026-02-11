package com.slotmachine.model

/**
 * Defines the pay lines on a 3-reel, 3-row slot machine display.
 * Each pay line is defined by which row index (0=top, 1=middle, 2=bottom)
 * to read from each of the 3 reels.
 */
enum class PayLine(
    val displayName: String,
    val positions: List<Int> // row index for reel 0, reel 1, reel 2
) {
    TOP("Top", listOf(0, 0, 0)),
    MIDDLE("Middle", listOf(1, 1, 1)),
    BOTTOM("Bottom", listOf(2, 2, 2)),
    DIAGONAL_DOWN("Diagonal ↘", listOf(0, 1, 2)),
    DIAGONAL_UP("Diagonal ↗", listOf(2, 1, 0));

    /**
     * Extract the symbols on this pay line from a set of 3 reel windows.
     * Each reel window is a list of 3 visible symbols (top, middle, bottom).
     */
    fun getSymbols(reelWindows: List<List<Symbol>>): List<Symbol> {
        return positions.mapIndexed { reelIndex, rowIndex ->
            reelWindows[reelIndex][rowIndex]
        }
    }
}
