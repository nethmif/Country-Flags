package com.example.nethmifernandow1953525

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

class AdvancedLevel : ComponentActivity() {
    // MutableState variables to hold state information
    var countryCodes = mutableStateListOf("", "", "")
    var countryNames = mutableStateListOf("", "", "")
    var givenCountryNames = mutableStateListOf("", "", "")
    var submittedCountryNames = mutableStateListOf("", "", "")
    var isSubmitted = mutableStateOf(false)
    var failedAttempts = mutableIntStateOf(0)
    var isAllCorrect = mutableStateOf(false)


    var score = mutableIntStateOf(0)
    var scoreForInputs = mutableStateListOf(0, 0, 0)
    val isTimerEnabled = mutableStateOf(false)

    var timeout = mutableIntStateOf(10)
    var refreshToken = mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isTimerEnabled.value = intent.getBooleanExtra("ENABLE_TIMER", false)


        if(savedInstanceState != null) {
            for (index in 0..<3) {
                countryCodes[index] = savedInstanceState.getStringArray("countryCodes")?.get(index).toString()
                countryNames[index] = savedInstanceState.getStringArray("countryNames")?.get(index).toString()
                givenCountryNames[index] = savedInstanceState.getStringArray("givenCountryNames")?.get(index).toString()
                submittedCountryNames[index] = savedInstanceState.getStringArray("submittedCountryNames")?.get(index).toString()
                scoreForInputs[index] = savedInstanceState.getIntArray("scoreForInputs")?.get(index)!!
            }
            isSubmitted.value = savedInstanceState.getBoolean("isSubmitted", false)
            failedAttempts.intValue = savedInstanceState.getInt("failedAttempts", 0)
            isAllCorrect.value = savedInstanceState.getBoolean("isAllCorrect", false)
            score.intValue = savedInstanceState.getInt("score", 0)
            timeout.intValue = savedInstanceState.getInt("timeout", 10)
        }
        setContent {
            loadRandomFlags()
            BuildContent()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArray("countryCodes", countryCodes.toTypedArray())
        outState.putStringArray("countryNames", countryNames.toTypedArray())
        outState.putStringArray("givenCountryNames", givenCountryNames.toTypedArray())
        outState.putStringArray("submittedCountryNames", submittedCountryNames.toTypedArray())
        outState.putIntArray("scoreForInputs", scoreForInputs.toIntArray())

        outState.putBoolean("isSubmitted", isSubmitted.value)
        outState.putInt("failedAttempts", failedAttempts.intValue)
        outState.putBoolean("isAllCorrect", isAllCorrect.value)
        outState.putInt("score", score.intValue)
        outState.putInt("timeout", timeout.intValue)

    }


    @Composable
    fun BuildContent() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            Button(
                onClick = {
                    val intent = Intent(this@AdvancedLevel, MainActivity::class.java)
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
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isTimerEnabled.value && failedAttempts.intValue < 3) {
                    CountdownTimer(
                        refreshToken = refreshToken.intValue,
                        timeout = timeout.intValue,
                        onTimerTick = {eta->
                            timeout.intValue = eta
                        },
                        onTimeout = {
                            submit()
                            timeout.intValue = 10
                        }
                    )
                }

                Text(
                    text = "Score: " + score.intValue,
                    modifier = Modifier
                        .padding(all = 20.dp),
                    style = TextStyle(textAlign = TextAlign.End),
                    fontSize = 25.sp
                )
            }



            // Determine if results should be shown
            var shouldShowResults = failedAttempts.value >= 3 || isAllCorrect.value

            // Display flags and input fields
            for (index in 0 until countryCodes.size) {
                val countryCode = countryCodes[index]
                val countryName = Countries.countriesMap[countryCode]

//                Log.i("SDDSDSDSDS", countryName!!);
                val resourceId = resources.getIdentifier(
                    countryCode.lowercase(Locale.getDefault()),
                    "drawable",
                    packageName
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp)
                ) {

                    // Determine text color based on correctness
                    val isCorrect = submittedCountryNames[index].lowercase() == countryName?.lowercase()
                    var color = Color.Black
                    if (isSubmitted.value) {
                        color = if (isCorrect) Color.Green else Color.Red
                    }

                    Column {
                        // Display flag image
                        Image(
                            modifier = Modifier
                                .width(150.dp)
                                .height(100.dp)
                                .padding(
                                    end = 20.dp
                                ),
                            painter = painterResource(id = resourceId),
                            contentDescription = null
                        )


                    }

                    Column {
                        // Create outlined text field for user input
                        var error :String? = null;
                        if(givenCountryNames[index].isEmpty()){
                            error = "This is required!"
                        }

                        Column (

                        ){
                            OutlinedTextField(
                                isError = error != null && isSubmitted.value,
                                textStyle = TextStyle(color = color, fontSize = 20.sp),
                                enabled = !(isCorrect || shouldShowResults),
                                value = givenCountryNames[index],
                                onValueChange = {
                                    givenCountryNames[index] = it
                                }
                            )
//                            error?.let {
//                                if(!shouldShowResults && isSubmitted.value) {
//                                    Text(
//                                        text = error,
//                                        style = MaterialTheme.typography.labelSmall,
//                                        color = Color.Red,
//                                    )
//                                }
//                            }
                        }
                        if(shouldShowResults && !isCorrect) {
                            // Display country name
                            Text(
                                text = countryName!!,
                                color = Color.Blue,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    }
                }
            }

            // Display result text and next button
            if (shouldShowResults) {
                if (isAllCorrect.value) {
                    Text(
                        text = "Correct!",
                        fontSize = 30.sp,
                        color = Color.Green,
                        modifier = Modifier.padding(top = 40.dp)
                    )
                }

                if (isFailedAttemptsExceeded()) {
                    Text(
                        text = "WRONG!",
                        fontSize = 30.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 40.dp)
                    )
                }

                Button(
                    onClick = { reset() },
                    modifier = Modifier
                        .width(275.dp)
                        .padding(all = 40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(red = 25, green = 25, blue = 112)
                    ),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(text = "Next", fontSize = 25.sp)
                }
            } else {
                // Display submit button
                Button(
                    onClick = {
                        submit()
                    },
                    modifier = Modifier
                        .width(275.dp)
                        .padding(all = 40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(red = 25, green = 25, blue = 112)
                    ),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(text = "Submit", fontSize = 25.sp)
                }
            }
        }
    }

    private fun submit(){
        for (index in 0 until countryCodes.size) {
            submittedCountryNames[index] = givenCountryNames[index]
        }
        isSubmitted.value = true

        if (isAllCorrect()) {
            isAllCorrect.value = true
        } else {
            failedAttempts.value++
        }


        refreshToken.value++;
        updateScore()
    }
    // Check if failed attempts exceeded
    private fun isFailedAttemptsExceeded(): Boolean {
        return failedAttempts.value >= 3
    }

    // Reset state for next round
    private fun reset() {
        for (i in 0 until 3) {
            countryCodes[i] = ""
            countryNames[i] = ""
            givenCountryNames[i] = ""
            submittedCountryNames[i] = ""
            scoreForInputs[i] = 0
        }

        isSubmitted.value = false
        failedAttempts.value = 0
        isAllCorrect.value = false
    }

    // Check if all answers are correct
    private fun isAllCorrect(): Boolean {
        var allCorrectStatus = true
        for (index in 0 until countryCodes.size) {
            val countryCode = countryCodes[index]
            val countryName = Countries.countriesMap[countryCode]

            allCorrectStatus = allCorrectStatus && (submittedCountryNames[index].lowercase() == countryName?.lowercase())
        }

        return allCorrectStatus
    }

    private fun updateScore() {
        for (index in 0 until countryCodes.size) {
            if(scoreForInputs[index] > 0) continue;

            val countryCode = countryCodes[index]
            val countryName = Countries.countriesMap[countryCode]

            val isCorrect = (submittedCountryNames[index].lowercase() == countryName?.lowercase())
            if(isCorrect){
                score.value++;
                scoreForInputs[index] = 1
            }
        }
    }

    // Load random flags
    private fun loadRandomFlags() {
        if (countryCodes[0] != "") return

        for (i in 0 until 3) {
            countryCodes[i] = Countries.countriesMap.keys.random()
        }
    }
}
