package com.fitapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import com.fitapp.android.api.RetrofitInstance
import com.fitapp.android.model.LoginRequest
import com.fitapp.android.model.RegisterRequest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}

@Composable
fun App() {
    var isLoggedIn by remember { mutableStateOf(false) }

    if (isLoggedIn) {
        MainScreen()
    } else {
        AuthScreen(
            onLoginSuccess = { isLoggedIn = true }
        )
    }
}

@Composable
fun AuthScreen(onLoginSuccess: () -> Unit) {
    val scope = rememberCoroutineScope()

    var isLoginMode by remember { mutableStateOf(true) }

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isLoginMode) "Login" else "Register",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!isLoginMode) {
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        val response = if (isLoginMode) {
                            RetrofitInstance.api.login(
                                LoginRequest(
                                    email = email,
                                    password = password
                                )
                            )
                        } else {
                            RetrofitInstance.api.register(
                                RegisterRequest(
                                    username = username,
                                    email = email,
                                    password = password
                                )
                            )
                        }

                        message = response.message

                        if (response.success) {
                            onLoginSuccess()
                        }
                    } catch (e: Exception) {
                        message = "Error: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoginMode) "Login" else "Register")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = {
                isLoginMode = !isLoginMode
                message = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoginMode) "Go to Register" else "Go to Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = message)
    }
}

@Composable
fun MainScreen() {
    val scope = rememberCoroutineScope()

    var foodName by remember { mutableStateOf("") }
    var grams by remember { mutableStateOf("") }

    var dailyCalories by remember { mutableStateOf(0.0) }
    var dailyProtein by remember { mutableStateOf(0.0) }
    var dailyCarbs by remember { mutableStateOf(0.0) }
    var dailyFat by remember { mutableStateOf(0.0) }

    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Daily calories tracker",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = foodName,
            onValueChange = { foodName = it },
            label = { Text("Food name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = grams,
            onValueChange = { grams = it },
            label = { Text("Grams") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        val response = RetrofitInstance.api.analyzeFood(
                            com.fitapp.android.model.FoodRequest(
                                foodName = foodName,
                                grams = grams.toDoubleOrNull() ?: 0.0
                            )
                        )

                        dailyCalories += response.calories
                        dailyProtein += response.protein
                        dailyCarbs += response.carbs
                        dailyFat += response.fat

                        message = "${response.foodName} added"

                        foodName = ""
                        grams = ""

                    } catch (e: Exception) {
                        message = "Error: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add food")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Calories: %.1f".format(dailyCalories))
        Text("Protein: %.1f g".format(dailyProtein))
        Text("Carbs: %.1f g".format(dailyCarbs))
        Text("Fat: %.1f g".format(dailyFat))

        Spacer(modifier = Modifier.height(16.dp))

        Text(message)
    }
}