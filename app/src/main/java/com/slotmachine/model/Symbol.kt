package com.slotmachine.model

/**
 * Traditional slot machine symbols ordered by value (lowest to highest).
 * Each symbol has a display character, name, payout multiplier for 3-of-a-kind,
 * and a weight that controls how frequently it appears on the reels.
 */
enum class Symbol(
    val displayChar: String,
    val displayName: String,
    val payout3x: Int,
    val payout2x: Int,
    val weight: Int
) {
    CHERRY("🍒", "Cherry", 5, 2, 25),
    LEMON("🍋", "Lemon", 8, 3, 22),
    ORANGE("🍊", "Orange", 10, 4, 20),
    PLUM("🍇", "Plum", 15, 5, 18),
    BELL("🔔", "Bell", 20, 6, 14),
    DIAMOND("💎", "Diamond", 50, 10, 8),
    BAR("🎰", "BAR", 100, 15, 5),
    SEVEN("7️⃣", "Seven", 250, 25, 3);

    companion object {
        /** Build a weighted pool for random reel strip generation. */
        fun weightedPool(): List<Symbol> {
            return entries.flatMap { symbol ->
                List(symbol.weight) { symbol }
            }
        }
    }
}
