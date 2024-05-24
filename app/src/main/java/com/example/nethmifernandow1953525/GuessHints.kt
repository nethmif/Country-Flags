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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import com.example.nethmifernandow1953525.ui.theme.NethmiFernandow1953525Theme

class GuessHints : ComponentActivity() {

    // Declaring mutable states for various properties
    private lateinit var countryCode: MutableState<String?>
    private lateinit var countryName: MutableState<String?>
    private lateinit var correctGuesses: MutableState<Array<Char?>>
    private lateinit var guessedChar: MutableState<String>
    private lateinit var incorrectAttempts: MutableState<Int>
    private lateinit var isTimerEnabled: MutableState<Boolean>
    private lateinit var  timerTimeout: MutableState<Int>
    private lateinit var  refreshToken: MutableState<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initializing mutable states with saved or default values
        countryCode = mutableStateOf(savedInstanceState?.getString("COUNTRY_CODE"))
        countryName = mutableStateOf(savedInstanceState?.getString("COUNTRY_NAME"))
        correctGuesses = mutableStateOf(savedInstanceState?.getCharArray("CORRECT_GUESSES")?.map { it }?.toTypedArray() ?: emptyArray())
        guessedChar = mutableStateOf(savedInstanceState?.getString("GUESSED_CHAR") ?: "")
        incorrectAttempts = mutableIntStateOf(savedInstanceState?.getInt("INCORRECT_ATTEMPTS") ?: 0)
        timerTimeout = mutableIntStateOf(savedInstanceState?.getInt("timerTimeout") ?: 10)
        isTimerEnabled = mutableStateOf(savedInstanceState?.getBoolean("ENABLE_TIMER_1") ?: false)
        refreshToken = mutableStateOf(savedInstanceState?.getInt("refreshToken") ?: 0)
        isTimerEnabled.value = intent.getBooleanExtra("ENABLE_TIMER", false)

        // Setting the content using Compose
        setContent {
            BuildContent();
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Saving current state into the outState bundle
        outState.putString("COUNTRY_CODE", countryCode.value)
        outState.putString("COUNTRY_NAME", countryName.value)
        outState.putCharArray("CORRECT_GUESSES", correctGuesses.value.toString().toCharArray())
        outState.putString("GUESSED_CHAR", guessedChar.value)
        outState.putInt("INCORRECT_ATTEMPTS", incorrectAttempts.value)
        outState.putBoolean("isTimerEnabled", isTimerEnabled.value)
        outState.putInt("timerTimeout", timerTimeout.value)
        outState.putInt("refreshToken", refreshToken.value)
    }

    @Composable
    fun BuildContent(){

        // Generating random country code and name if not initialized
        if(countryCode.value == null){
            countryCode.value = Countries
                .countriesMap
                .keys
                .random();

            countryName.value = Countries
                .countriesMap[countryCode.value]
                .toString()

            correctGuesses.value = arrayOfNulls(countryName.value!!.length)
        }

        // Getting the resource ID for the flag image
        val resourceId = resources.getIdentifier(
            countryCode.value!!.lowercase(Locale.getDefault()),
            "drawable",
            packageName
        );

        // Creating placeholder for country name with hidden characters
        val countryNamePlaceholder_ = countryName.value?.toCharArray();
        for (index in countryName.value!!.indices) {
            if(correctGuesses.value.count() < index + 1)
                continue

            if(correctGuesses.value[index] == null){
                countryNamePlaceholder_?.set(index, '-')
            }
        }
        val countryNamePlaceholder = countryNamePlaceholder_?.joinToString("")

        // Composable function for building UI content
        NethmiFernandow1953525Theme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 50.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Button(
                        onClick = {
                            val intent = Intent(this@GuessHints, MainActivity::class.java)
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
                    // Display countdown timer if enabled
                    if(isTimerEnabled.value && incorrectAttempts.value < 3 && !isAllGuessesCorrect()) {
                        CountdownTimer(
                            refreshToken = refreshToken.value,
                            timeout = timerTimeout.value,
                            onTimerTick = {eta ->
                                timerTimeout.value = eta
                            },
                            onTimeout = {
                                submitAnswer()
                                refreshToken.value++
                                timerTimeout.value = 10
//                                incorrectAttempts.value = 3
                            }
                        )
                    }

                    // Display flag image
                    Image(
                        modifier = Modifier
                            .width(200.dp)
                            .height(100.dp),
                        painter = painterResource(id = resourceId),
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.padding(24.dp))

                    // Display country name placeholder
                    Text(countryNamePlaceholder!!)

                    Spacer(modifier = Modifier.padding(24.dp))

                    // Display input field for guessing
                    if(incorrectAttempts.value < 3) {
                        if(isAllGuessesCorrect()) {
                            // Display "Correct!" message and "Next" button
                            Text(text = "CORRECT!", fontSize = 30.sp, color = Color.Green)
                            Spacer(modifier = Modifier.padding(24.dp))
                            Button(
                                onClick = {
                                    resetGame()
                                },
                                modifier = Modifier
                                    .height(70.dp)
                                    .width(275.dp)
                                    .padding(top = 0.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(red = 25, green = 25, blue = 112)
                                )
                            ) {
                                Text(text = "Next", fontSize = 25.sp)
                            }
                        }else{
                            // When the text field is left empty and submitted an error message is displayed
                            var error :String? = null;
                            if(guessedChar.value.isEmpty()){
                                error = "This is required!"
                            }

                            Column (
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Display input field for guessing and "Submit" button
                                OutlinedTextField(
                                    isError = error != null && incorrectAttempts.value > 0,
                                    value = guessedChar.value,
                                    onValueChange = {
                                        guessedChar.value = it
                                    },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Done
                                    ),
                                    maxLines = 1,
                                    modifier = Modifier
                                        .width(100.dp)
                                        .align(Alignment.CenterHorizontally),
                                    textStyle = TextStyle(textAlign = TextAlign.Center)
                                )
                                error?.let {
                                    if(incorrectAttempts.value >= 1 && (incorrectAttempts.value < 3)) {
                                        Text(
                                            text = error,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Red,
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.padding(24.dp))

                            Button(
                                onClick = {
                                    submitAnswer()
                                },
                                modifier = Modifier
                                    .height(60.dp)
                                    .width(200.dp)
                                    .padding(top = 0.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(red = 25, green = 25, blue = 112)
                                ),
                                shape = RoundedCornerShape(15.dp)
                            ) {
                                Text(text = "Submit", fontSize = 25.sp)
                            }

                        }
                    }else{
                        // Display "WRONG!" message, correct flag name and "Next" button
                        Text(text = "WRONG!", fontSize = 30.sp, color = Color.Red)
                        Text(text = "The correct flag is", fontSize = 20.sp, color = Color.Blue)
                        Text(text = countryName.value!!, fontSize = 20.sp, color = Color.Blue)
                        Spacer(modifier = Modifier.padding(24.dp))
                        Button(
                            onClick = {
                                resetGame()
                            },
                            modifier = Modifier
                                .height(50.dp)
                                .width(225.dp)
                                .padding(top = 0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(red = 25, green = 25, blue = 112)
                            ),
                            shape = RoundedCornerShape(15.dp)
                        ) {
                            Text(text = "Next", fontSize = 25.sp)
                        }
                    }

                }
            }
        }
    }

    // Function to submit guessed answer
    private fun submitAnswer(){
        var isCorrect = false;
        val countryNameLocal = countryName.value!!.toCharArray();
        for (index in countryNameLocal.indices) {
            if (guessedChar.value.isNotEmpty()) {
                if (countryNameLocal[index].lowercase() == guessedChar.value[0].lowercase()) {
                    correctGuesses.value[index] = countryNameLocal[index];
                    correctGuesses.value = correctGuesses.value.copyOf()

                    isCorrect = true
                }
            } else {
                break;
            }
        }

        guessedChar.value = "";
        if (!isCorrect) {
            incorrectAttempts.value++
        }

        timerTimeout.value = 20
    }

    // Function to check if all guesses are correct
    private fun isAllGuessesCorrect(): Boolean {
        var isCorrect: Boolean = true;
        for(i in countryName.value?.indices!!){
            if(countryName.value?.get(i) != correctGuesses.value[i]){
                isCorrect = false
            }
        }
        return isCorrect;
    }

    // Function to reset the game state
    private fun resetGame() {
        countryCode.value = null
        countryName.value = null
        correctGuesses.value = emptyArray<Char?>()
        guessedChar.value = ""
        incorrectAttempts.value = 0
        timerTimeout.value = 10
    }
}
