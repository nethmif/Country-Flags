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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import com.example.nethmifernandow1953525.ui.theme.NethmiFernandow1953525Theme

class GuessTheCountry : ComponentActivity() {
    private val countryCode = mutableStateOf<String?>(null) // MutableState<String>
    private val selectedCountryCode = mutableStateOf<String?>(null) // MutableState<String?>
    private val isSubmitted = mutableStateOf(false) // MutableState<Boolean>
    private val isTimerEnabled = mutableStateOf(false)
    private val timeout = mutableIntStateOf(10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isTimerEnabled.value = intent.getBooleanExtra("ENABLE_TIMER", false)

        if (savedInstanceState != null) {
            countryCode.value = savedInstanceState.getString("countryCode");
            selectedCountryCode.value = savedInstanceState.getString("selectedCountryCode");
            isSubmitted.value = savedInstanceState.getBoolean("isSubmitted");
            isTimerEnabled.value = savedInstanceState.getBoolean("isTimerEnabled");
            timeout.intValue = savedInstanceState.getInt("timeout_1");
        }

        setContent {
            if(countryCode.value == null){
                countryCode.value = Countries.countriesMap.keys.random();
            }


            val resourceId = resources.getIdentifier(
                countryCode.value?.lowercase(Locale.getDefault()),
                "drawable",
                packageName
            );

            val items = Countries.countriesMap.entries.toList();

            NethmiFernandow1953525Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 10.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ){
                        Button(
                            onClick = {
                                val intent = Intent(this@GuessTheCountry, MainActivity::class.java)
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
                        if(isTimerEnabled.value && !isSubmitted.value) {
                            CountdownTimer(
                                refreshToken = 0,
                                timeout = timeout.intValue,
                                onTimerTick = {eta ->
                                    timeout.intValue = eta
                                },
                                onTimeout = {
                                    isSubmitted.value = true;
                                    timeout.intValue = 10;
                                }
                            )
                        }

                        Column(modifier = Modifier.fillMaxHeight(.3f)) {
                            Image(
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(100.dp),
                                painter = painterResource(id = resourceId),
                                contentDescription = null
                            )

                            Spacer(modifier = Modifier.padding(24.dp))
                        }

                        if(!isSubmitted.value) {

                            Column(modifier = Modifier.fillMaxHeight(.8f)) {
                                ClickableList(items = items) { item ->
                                    selectedCountryCode.value = item.key;
                                }
                            }

                            Column(modifier = Modifier) {
                                Button(
                                    enabled = selectedCountryCode.value != null,
                                    onClick = {
                                        isSubmitted.value = true;
                                    },
                                    modifier = Modifier
                                        .width(275.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(red = 25, green = 25, blue = 112)
                                    ),
                                    shape = RoundedCornerShape(15.dp)
                                ) {
                                    Text(text = "Submit", fontSize = 25.sp)
                                }
                            }
                        }else{
                            Column(
                                modifier = Modifier,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if(selectedCountryCode.value?.uppercase() == countryCode.value){
                                    Text("CORRECT!", color= Color.Green,  fontSize = 30.sp)
                                }else{
                                    Text("WRONG!", color= Color.Red, fontSize = 30.sp)

                                    Text("The correct country is", color= Color.Blue, fontSize = 20.sp)
                                    Countries.countriesMap[countryCode.value]?.let {
                                        Text(it, color= Color.Blue, fontSize = 20.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.padding(24.dp))

                                Button(
                                    onClick = {
                                        isSubmitted.value = false;
                                        selectedCountryCode.value = null;
                                        countryCode.value = Countries.countriesMap.keys.random();
                                    },
                                    modifier = Modifier
                                        .width(275.dp)
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
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("countryCode", countryCode.value)
        outState.putString("selectedCountryCode", selectedCountryCode.value)
        outState.putBoolean("isSubmitted", isSubmitted.value)
        outState.putBoolean("isTimerEnabled", isTimerEnabled.value)
        outState.putInt("timeout_1", timeout.intValue)
    }


    @Composable
    fun ClickableList(items: List<Map.Entry<String, String>>, onItemClick: (Map.Entry<String, String>) -> Unit) {

        LazyColumn (modifier =  Modifier.padding(20.dp)) {
            items(items) { item ->

                var isSelected  = selectedCountryCode.value == item.key
                val textColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black
                val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal

                Text(item.value,
                    color = textColor,
                    fontWeight = fontWeight,
                    fontSize = 25.sp,
                    modifier = Modifier
                        .clickable {
                            isSelected = !isSelected
                            onItemClick(item)
                        }
                        .padding(20.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}
