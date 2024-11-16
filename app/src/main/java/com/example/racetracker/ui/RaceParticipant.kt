import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class RaceParticipant(
    val name: String,
    val maxProgress: Int = 100,
    val progressDelayMillis: Long = 500L,
    private val progressIncrement: Int = 1,
    private val initialProgress: Int = 0
) {
    init {
        require(maxProgress > 0) { "maxProgress=$maxProgress; must be > 0" }
        require(progressIncrement > 0) { "progressIncrement=$progressIncrement; must be > 0" }
    }

    var currentProgress = initialProgress
        private set

    suspend fun run() = coroutineScope {
        try {
            while (currentProgress < maxProgress && isActive) {
                println("Delaying for $progressDelayMillis ms")
                delay(progressDelayMillis)
                println("Incrementing progress")
                currentProgress = (currentProgress + progressIncrement).coerceAtMost(maxProgress)
                println("Current progress: $currentProgress")
            }
        } catch (e: CancellationException) {
            Log.e("RaceParticipant", "$name: Race was cancelled: ${e.message}")
            throw e
        }
    }


    fun reset() {
        currentProgress = 0
    }
}

val RaceParticipant.progressFactor: Float
    get() = currentProgress / maxProgress.toFloat()
