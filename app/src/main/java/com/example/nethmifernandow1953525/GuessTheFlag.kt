package com.example.nethmifernandow1953525

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

class GuessTheFlag : ComponentActivity() {
    // Mutable state for the correct country code, list of country codes, and selected country code
    var correctCountryCode = mutableStateOf<String?>(null)
    var countryCodes = mutableStateListOf<String?>(null, null, null)
    var selectedCountryCode = mutableStateOf<String?>(null)
    // Mutable state for enabling/disabling the timer
    val isTimerEnabled = mutableStateOf(false)
    val timeout = mutableIntStateOf(10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get timer state from intent
        isTimerEnabled.value = intent.getBooleanExtra("ENABLE_TIMER", false)

        // Restore saved instance state
        correctCountryCode.value = savedInstanceState?.getString("correctCountryCode")
        selectedCountryCode.value = savedInstanceState?.getString("selectedCountryCode")
        countryCodes[0] = savedInstanceState?.getStringArray("countryCodes")?.get(0)
        countryCodes[1] = savedInstanceState?.getStringArray("countryCodes")?.get(1)
        countryCodes[2] = savedInstanceState?.getStringArray("countryCodes")?.get(2)
        timeout.intValue = savedInstanceState?.getInt("timeout") ?: 10

        // Set content using Jetpack Compose
        setContent {
            // Load random flags if necessary and build content
            loadRandomFlags()
            BuildContent()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save instance state
        outState.putString("correctCountryCode", correctCountryCode.value)
        outState.putString("selectedCountryCode", selectedCountryCode.value)
        outState.putStringArray("countryCodes", countryCodes.toTypedArray())
        outState.putInt("timeout", timeout.intValue)

    }

    @Composable
    fun BuildContent(){
        // Column layout for displaying content vertically
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    val intent = Intent(this@GuessTheFlag, MainActivity::class.java)
                    startActivity(intent)
                },
                modifier = Modifier
                    .width(275.dp)
                    .padding(all = 40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(red = 25, green = 25, blue = 112)
                ),
                shape = RoundedCornerShape(15.dp)
            ) {
                Text(text = "Home", fontSize = 25.sp)
            }
            // Display countdown timer if enabled and no country is selected yet
            if(isTimerEnabled.value && selectedCountryCode.value == null) {
                CountdownTimer(
                    timeout = timeout.value,
                    onTimerTick = {eta ->
                        timeout.intValue = eta
                    },
                    onTimeout = {
                        selectedCountryCode.value = ""
                        timeout.intValue = 10
                    }
                )
            }

            // Display the name of the correct country
            val correctCountryCodeName = Countries.countriesMap[correctCountryCode.value]
            Text(
                text = correctCountryCodeName.toString(),
                style = TextStyle(fontSize = 40.sp, textAlign = TextAlign.Center),
                modifier = Modifier.padding(top = 40.dp, bottom = 40.dp)
            )

            // Display options for selecting countries
            if(selectedCountryCode.value == null) {
                for (countryCode in countryCodes) {
                    val resourceId = resources.getIdentifier(
                        countryCode?.lowercase(Locale.getDefault()),
                        "drawable",
                        packageName
                    )
                    Image(
                        modifier = Modifier
                            .width(225.dp)
                            .height(125.dp)
                            .clickable(onClick = {
                                selectedCountryCode.value = countryCode
                            })
                            .padding(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp),
                        painter = painterResource(id = resourceId),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }else{
                // Display feedback for correct or wrong selection
                if(selectedCountryCode.value == correctCountryCode.value) {
                    Text(text = "CORRECT!", fontSize = 30.sp, color = Color.Green)
                }else{
                    Text(text = "WRONG!", fontSize = 30.sp, color = Color.Red)
                }
            }

            // Display next button after selecting a country
            if(selectedCountryCode.value != null) {
                Button(
                    onClick = {
                        // Reset states for the next round
                        countryCodes[0] = null
                        countryCodes[1] = null
                        countryCodes[2] = null
                        correctCountryCode.value = null
                        selectedCountryCode.value = null
                    },
                    modifier = Modifier
                        .width(275.dp)
                        .padding(top = 40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(red = 25, green = 25, blue = 112)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = "Next", fontSize = 25.sp)
                }
            }
        }
    }

    private fun loadRandomFlags(){
        // Check if flags are already loaded
        if(countryCodes[0] != null) return

        // Load random flags for the first time
        for (i in 0..2) {
            countryCodes[i] = Countries.countriesMap.keys.random()
        }

        correctCountryCode.value = countryCodes.random()
    }
}
