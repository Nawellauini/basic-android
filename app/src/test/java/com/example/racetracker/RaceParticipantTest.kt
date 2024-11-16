package com.example.racetracker

import RaceParticipant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RaceParticipantTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val raceParticipant = RaceParticipant(
        name = "Test",
        maxProgress = 100,
        progressDelayMillis = 500L,
        initialProgress = 0,
        progressIncrement = 1
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun raceParticipant_RaceStarted_ProgressUpdated() = runTest {
        val expectedProgress = 1

        val job = launch { raceParticipant.run() }
        println("Initial Progress: ${raceParticipant.currentProgress}")

        advanceTimeBy(raceParticipant.progressDelayMillis)
        runCurrent()

        println("Progress after advancing time: ${raceParticipant.currentProgress}")
        assertEquals(expectedProgress, raceParticipant.currentProgress)


    }
    @Test
    fun raceParticipant_RaceFinished_ProgressUpdated() = runTest {
        launch { raceParticipant.run() }
        advanceTimeBy(raceParticipant.maxProgress * raceParticipant.progressDelayMillis)
        runCurrent()
        assertEquals(100, raceParticipant.currentProgress)

    }
}
