package futur.apps.composeproject1.viewmodels

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * QuizTimer - Coroutine-based countdown timer with precise control.
 */
class QuizTimer(
    private val scope: CoroutineScope,
    private val tickInterval: Long,         // e.g. 1000L
    private val onTick: (Int) -> Unit,
    private val onFinish: () -> Unit
) {
    private var job: Job? = null
    private var timeLeft: Int = 0
    private var isPaused: Boolean = false

    fun start(initialTime: Int) {
        stop() // cancel any running job
        timeLeft = initialTime
        isPaused = false

        job = scope.launch {
            // Immediately emit the first tick (UI shows starting time)
            onTick(timeLeft)

            while (isActive && timeLeft > 0) {
                delay(tickInterval)

                if (!isPaused) {
                    timeLeft--

                    if (timeLeft > 0) {
                        onTick(timeLeft)
                    } else {
                        // Time is up → emit 0 and finish once
                        onTick(0)
                        onFinish()
                        break
                    }
                }
            }
        }
    }

    fun pause() {
        isPaused = true
    }

    fun resume() {
        if (isPaused) isPaused = false
    }

    fun stop() {
        job?.cancel()
        job = null
        isPaused = false
    }

    fun isRunning(): Boolean = job?.isActive == true && !isPaused
}


/**
 * QuizTimer
 *
 * A simple coroutine-based countdown timer.
 * Used by QuizViewModel to manage per-question time limits.
 *
 * Features:
 * - Starts with a given initial time
 * - Calls `onTick()` every interval with updated timeLeft
 * - Calls `onFinish()` when time reaches 0
 * - Can be paused, resumed, or stopped manually
 *
 * Why a separate class?
 * ✅ Keeps timer logic isolated (Single Responsibility Principle)
 * ✅ No Android dependencies → easily testable
 * ✅ Reusable in other projects (not tied to Quiz only)
 */
/**
     * Start the timer with a given initial time.
     * If already running, the old job is cancelled and restarted.
     *
     * @param initialTime time in seconds
     */
/**
     * Pause the timer (stops decrementing but keeps the coroutine alive).
     */
/**
     * Resume the timer if it was paused.
     */
/**
     * Stop the timer completely.
     * Cancels the coroutine and resets internal flags.
     */