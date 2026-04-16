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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.layout.*


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

    var showAddFoodDialog by remember { mutableStateOf(false) }

    var foodName by remember { mutableStateOf("") }
    var grams by remember { mutableStateOf("") }

    var meals by remember { mutableStateOf(listOf<com.fitapp.android.model.MealItem>()) }
    var message by remember { mutableStateOf("") }

    val totalCalories = meals.sumOf { it.calories }
    val totalProtein = meals.sumOf { it.protein }
    val totalCarbs = meals.sumOf { it.carbs }
    val totalFat = meals.sumOf { it.fat }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Today",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Total calories eaten",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${"%.1f".format(totalCalories)} kcal",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Protein: ${"%.1f".format(totalProtein)} g")
                    Text("Carbs: ${"%.1f".format(totalCarbs)} g")
                    Text("Fat: ${"%.1f".format(totalFat)} g")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    message = "Previous days screen later"
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View previous days")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    showAddFoodDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add food")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Today's foods",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (meals.isEmpty()) {
                Text("No foods added yet")
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(meals) { meal ->
                        MealCard(meal = meal)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            if (message.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(message)
            }
        }

        if (showAddFoodDialog) {
            AddFoodDialog(
                foodName = foodName,
                grams = grams,
                onFoodNameChange = { foodName = it },
                onGramsChange = { grams = it },
                onDismiss = {
                    showAddFoodDialog = false
                    foodName = ""
                    grams = ""
                },
                onAddClick = {
                    scope.launch {
                        try {
                            val response = RetrofitInstance.api.analyzeFood(
                                com.fitapp.android.model.FoodRequest(
                                    foodName = foodName,
                                    grams = grams.toDoubleOrNull() ?: 0.0
                                )
                            )

                            val newMeal = com.fitapp.android.model.MealItem(
                                foodName = response.foodName,
                                grams = response.grams,
                                calories = response.calories,
                                protein = response.protein,
                                carbs = response.carbs,
                                fat = response.fat
                            )

                            meals = meals + newMeal
                            message = "${response.foodName} added"

                            showAddFoodDialog = false
                            foodName = ""
                            grams = ""

                        } catch (e: Exception) {
                            message = "Error: ${e.message}"
                        }
                    }
                }
            )
        }
    }
}
@Composable
fun MealCard(meal: com.fitapp.android.model.MealItem) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = meal.foodName,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text("${"%.0f".format(meal.grams)} g")
            Text("${"%.1f".format(meal.calories)} kcal")
            Text("Protein: ${"%.1f".format(meal.protein)} g")
            Text("Carbs: ${"%.1f".format(meal.carbs)} g")
            Text("Fat: ${"%.1f".format(meal.fat)} g")
        }
    }
}

@Composable
fun AddFoodDialog(
    foodName: String,
    grams: String,
    onFoodNameChange: (String) -> Unit,
    onGramsChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onAddClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add food")
        },
        text = {
            Column {
                TextField(
                    value = foodName,
                    onValueChange = onFoodNameChange,
                    label = { Text("Food name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = grams,
                    onValueChange = onGramsChange,
                    label = { Text("Grams") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onAddClick) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}