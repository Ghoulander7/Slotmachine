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
    val weight: Int
) {
    CHERRY("🍒", "Cherry", 5, 25),
    LEMON("🍋", "Lemon", 8, 22),
    ORANGE("🍊", "Orange", 10, 20),
    PLUM("🍇", "Plum", 15, 18),
    BELL("🔔", "Bell", 25, 12),
    DIAMOND("💎", "Diamond", 75, 6),
    BAR("🎰", "BAR", 150, 4),
    SEVEN("7️⃣", "Seven", 500, 2);

    companion object {
        /** Build a weighted pool for random reel strip generation. */
        fun weightedPool(): List<Symbol> {
            return entries.flatMap { symbol ->
                List(symbol.weight) { symbol }
            }
        }
    }
}
