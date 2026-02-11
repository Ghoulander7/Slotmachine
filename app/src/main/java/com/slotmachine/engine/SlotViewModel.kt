package com.slotmachine.engine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slotmachine.model.GameState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SlotViewModel : ViewModel() {

    private val engine = SlotEngine()

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    // Reel animation sequences for each reel
    private val _reelSequences = MutableStateFlow<List<List<com.slotmachine.model.Symbol>>>(emptyList())
    val reelSequences: StateFlow<List<List<com.slotmachine.model.Symbol>>> = _reelSequences.asStateFlow()

    private val _spinningReels = MutableStateFlow(listOf(false, false, false))
    val spinningReels: StateFlow<List<Boolean>> = _spinningReels.asStateFlow()

    fun spin() {
        val currentState = _state.value
        if (!currentState.canSpin) return

        val bet = currentState.bet

        // Deduct bet
        _state.update {
            it.copy(
                credits = it.credits - bet,
                isSpinning = true,
                wins = emptyList(),
                lastWinTotal = 0,
                message = "Spinning..."
            )
        }

        // Generate final results
        val finalWindows = engine.spin()

        // Generate animation sequences for each reel
        val sequences = finalWindows.map { window ->
            engine.getSpinSequence(window, extraSymbols = 15)
        }
        _reelSequences.value = sequences

        // Start all reels spinning
        _spinningReels.value = listOf(true, true, true)

        // Stop reels sequentially with delays
        viewModelScope.launch {
            delay(800)  // Reel 1 stops
            _spinningReels.value = listOf(false, true, true)
            _state.update { it.copy(reelWindows = listOf(finalWindows[0], it.reelWindows[1], it.reelWindows[2])) }

            delay(600)  // Reel 2 stops
            _spinningReels.value = listOf(false, false, true)
            _state.update { it.copy(reelWindows = listOf(finalWindows[0], finalWindows[1], it.reelWindows[2])) }

            delay(600)  // Reel 3 stops
            _spinningReels.value = listOf(false, false, false)
            _state.update { it.copy(reelWindows = finalWindows) }

            delay(200)

            // Evaluate wins
            val wins = engine.evaluateWins(finalWindows, bet)
            val totalWin = wins.sumOf { it.payout }

            _state.update {
                it.copy(
                    credits = it.credits + totalWin,
                    isSpinning = false,
                    wins = wins,
                    lastWinTotal = totalWin,
                    message = when {
                        totalWin > bet * 50 -> "JACKPOT! Won $totalWin credits!"
                        totalWin > 0 -> "Winner! +$totalWin credits!"
                        it.credits == 0 -> "No credits remaining. Game over!"
                        else -> "No win. Try again!"
                    }
                )
            }
        }
    }

    fun increaseBet() {
        _state.update {
            if (it.canIncreaseBet) {
                it.copy(bet = (it.bet + GameState.BET_STEP).coerceAtMost(GameState.MAX_BET))
            } else it
        }
    }

    fun decreaseBet() {
        _state.update {
            if (it.canDecreaseBet) {
                it.copy(bet = (it.bet - GameState.BET_STEP).coerceAtLeast(GameState.MIN_BET))
            } else it
        }
    }

    fun maxBet() {
        _state.update {
            if (!it.isSpinning) {
                it.copy(bet = GameState.MAX_BET.coerceAtMost(it.credits))
            } else it
        }
    }
}
