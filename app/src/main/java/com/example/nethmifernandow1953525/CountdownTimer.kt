package com.example.nethmifernandow1953525

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun CountdownTimer(
    refreshToken: Int = 0,
    timeout: Int = 10,
    onTimerTick: (eta: Int) -> Unit = {},
    onTimeout: () -> Unit
) {
    var countdownValue by remember { mutableIntStateOf(timeout) }

    LaunchedEffect(refreshToken) {
        countdownValue = timeout
    }
    LaunchedEffect(timeout) {
        countdownValue = timeout
    }

    LaunchedEffect(key1 = countdownValue) {
        while (countdownValue > 0) {
            delay(1000)
            countdownValue--
            onTimerTick(countdownValue);
        }
        onTimeout()
    }

    Text(
        text = "Time Left: $countdownValue",
        style = TextStyle(color = Color.Black),
        modifier = Modifier
            .padding(16.dp)
    )
}
