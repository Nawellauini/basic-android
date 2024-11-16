package com.example.racetracker.ui

import RaceParticipant
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.racetracker.R
import com.example.racetracker.ui.theme.RaceTrackerTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import progressFactor

@Composable
fun RaceTrackerApp() {
    // Players and race state
    val playerOne = remember { RaceParticipant(name = "Player 1", progressIncrement = 1) }
    val playerTwo = remember { RaceParticipant(name = "Player 2", progressIncrement = 2) }
    var raceInProgress by remember { mutableStateOf(false) }

    // Handle the race progress
    val scope = rememberCoroutineScope()
    LaunchedEffect(raceInProgress) {
        if (raceInProgress) {
            try {

                scope.launch { playerOne.run() }
                scope.launch { playerTwo.run() }
            } catch (e: CancellationException) {
                Log.e("RaceTrackerApp", "Race cancelled: ${e.message}")
            } finally {
                raceInProgress = false // End the race after both players finish
            }
        }
    }

    RaceTrackerScreen(
        playerOne = playerOne,
        playerTwo = playerTwo,
        isRunning = raceInProgress,
        onRunStateChange = { raceInProgress = it },
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.padding_medium))
    )
}

@Composable
private fun RaceTrackerScreen(
    playerOne: RaceParticipant,
    playerTwo: RaceParticipant,
    isRunning: Boolean,
    onRunStateChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.run_a_race),
            style = MaterialTheme.typography.headlineSmall,
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_walk),
                contentDescription = null,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
            )
            StatusIndicator(
                participantName = playerOne.name,
                currentProgress = playerOne.currentProgress,
                maxProgress = stringResource(R.string.progress_percentage, playerOne.maxProgress),
                progressFactor = playerOne.progressFactor,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(dimensionResource(R.dimen.padding_large)))

            StatusIndicator(
                participantName = playerTwo.name,
                currentProgress = playerTwo.currentProgress,
                maxProgress = stringResource(R.string.progress_percentage, playerTwo.maxProgress),
                progressFactor = playerTwo.progressFactor,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.size(dimensionResource(R.dimen.padding_large)))

            RaceControls(
                isRunning = isRunning,
                onRunStateChange = onRunStateChange,
                onReset = {
                    playerOne.reset()
                    playerTwo.reset()
                    onRunStateChange(false)
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun StatusIndicator(
    participantName: String,
    currentProgress: Int,
    maxProgress: String,
    progressFactor: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = participantName,
            modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small))
        )

        LinearProgressIndicator(
            progress = progressFactor,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.progress_indicator_height))
                .clip(RoundedCornerShape(dimensionResource(R.dimen.progress_indicator_corner_radius)))
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.progress_percentage, currentProgress),
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = maxProgress,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RaceControls(
    onRunStateChange: (Boolean) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
    isRunning: Boolean = true,
) {
    Column(
        modifier = modifier.padding(top = dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {
        Button(
            onClick = { onRunStateChange(!isRunning) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (isRunning) stringResource(R.string.pause) else stringResource(R.string.start))
        }
        OutlinedButton(
            onClick = onReset,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.reset))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RaceTrackerAppPreview() {
    RaceTrackerTheme {
        RaceTrackerApp()
    }
}