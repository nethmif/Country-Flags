package com.example.nethmifernandow1953525

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nethmifernandow1953525.ui.theme.NethmiFernandow1953525Theme


class MainActivity : ComponentActivity() {

    var isTimerEnabled = mutableStateOf(false)

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isTimerEnabled", isTimerEnabled.value)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState != null) {
            isTimerEnabled.value = savedInstanceState.getBoolean("isTimerEnabled", false)
        }

        setContent {
            NethmiFernandow1953525Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val context = LocalContext.current

                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.SpaceAround,
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        Button(
                            onClick = {
                                val intent = Intent(this@MainActivity, GuessTheCountry::class.java)
                                intent.putExtra("ENABLE_TIMER", isTimerEnabled.value)
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .height(70.dp)
                                .width(275.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(red = 25, green = 25, blue = 112)
                            ),
                            shape = RoundedCornerShape(15.dp)
                        ) {
                            Text(text = "Guess the Country", fontSize = 25.sp)
                        }
                        Button(
                            onClick = {
                                val intent = Intent(this@MainActivity, GuessHints::class.java)
                                intent.putExtra("ENABLE_TIMER", isTimerEnabled.value)
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .height(70.dp)
                                .width(275.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(red = 25, green = 25, blue = 112)
                            ),
                            shape = RoundedCornerShape(15.dp)
                        ) {
                            Text(text = "Guess Hints", fontSize = 25.sp)
                        }
                        Button(
                            onClick = {
                                val intent = Intent(this@MainActivity, GuessTheFlag::class.java)
                                intent.putExtra("ENABLE_TIMER", isTimerEnabled.value)
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .height(70.dp)
                                .width(275.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(red = 25, green = 25, blue = 112)
                            ),
                            shape = RoundedCornerShape(15.dp)
                        ) {
                            Text(text = "Guess the Flag", fontSize = 25.sp)
                        }
                        Button(
                            onClick = {
                                val intent = Intent(this@MainActivity, AdvancedLevel::class.java)
                                intent.putExtra("ENABLE_TIMER", isTimerEnabled.value)
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .height(70.dp)
                                .width(275.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(red = 25, green = 25, blue = 112)
                            ),
                            shape = RoundedCornerShape(15.dp)
                        ) {
                            Text(text = "Advanced Level", fontSize = 25.sp)
                        }

                        Row (
                            modifier = Modifier.padding(bottom = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Switch(
                                checked = isTimerEnabled.value,
                                onCheckedChange = { newSwitchValue ->
                                    isTimerEnabled.value = newSwitchValue
                                },
                                modifier = Modifier.padding(5.dp),
                            )
                            Text(text = "Switch")
                        }
                    }
                }
            }
        }
    }
}
