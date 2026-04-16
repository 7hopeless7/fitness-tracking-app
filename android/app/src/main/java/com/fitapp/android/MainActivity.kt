package com.fitapp.android

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fitapp.android.api.RetrofitInstance
import com.fitapp.android.model.LoginRequest
import com.fitapp.android.model.MealItem
import com.fitapp.android.model.MealRequest
import com.fitapp.android.model.RegisterRequest
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

@Composable
fun App() {
    var loggedInUserId by remember { mutableStateOf<Long?>(null) }

    if (loggedInUserId != null) {
        MainScreen(userId = loggedInUserId!!)
    } else {
        AuthScreen(onLoginSuccess = { userId -> loggedInUserId = userId })
    }
}

@Composable
fun AuthScreen(onLoginSuccess: (Long) -> Unit) {
    val scope = rememberCoroutineScope()

    var isLoginMode by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = if (isLoginMode) "Login" else "Register", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (!isLoginMode) {
            TextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
        }

        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            scope.launch {
                try {
                    val response = if (isLoginMode) {
                        RetrofitInstance.api.login(LoginRequest(email = email, password = password))
                    } else {
                        RetrofitInstance.api.register(RegisterRequest(username = username, email = email, password = password))
                    }

                    message = response.message
                    if (response.success && response.userId != null) {
                        onLoginSuccess(response.userId)
                    }
                } catch (e: Exception) {
                    message = "Error: ${e.message}"
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(if (isLoginMode) "Login" else "Register")
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { isLoginMode = !isLoginMode; message = "" }, modifier = Modifier.fillMaxWidth()) {
            Text(if (isLoginMode) "Go to Register" else "Go to Login")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(message)
    }
}

@Composable
fun MainScreen(userId: Long) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showAddFoodDialog by remember { mutableStateOf(false) }
    var editingMeal by remember { mutableStateOf<MealItem?>(null) }

    var foodName by remember { mutableStateOf("") }
    var grams by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    var meals by remember { mutableStateOf(emptyList<MealItem>()) }
    var message by remember { mutableStateOf("") }

    fun loadMeals() {
        scope.launch {
            try {
                meals = RetrofitInstance.api.getMeals(userId = userId, date = selectedDate.toString())
                message = ""
            } catch (e: Exception) {
                message = "Error: ${e.message}"
            }
        }
    }

    androidx.compose.runtime.LaunchedEffect(selectedDate) { loadMeals() }

    val totalCalories = meals.sumOf { it.calories }
    val totalProtein = meals.sumOf { it.protein }
    val totalCarbs = meals.sumOf { it.carbs }
    val totalFat = meals.sumOf { it.fat }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(text = "Meals", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Date: $selectedDate")
                OutlinedButton(onClick = {
                    DatePickerDialog(
                        context,
                        { _, y, m, d -> selectedDate = LocalDate.of(y, m + 1, d) },
                        selectedDate.year,
                        selectedDate.monthValue - 1,
                        selectedDate.dayOfMonth
                    ).show()
                }) { Text("Choose date") }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total calories", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${"%.1f".format(totalCalories)} kcal", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Protein: ${"%.1f".format(totalProtein)} g")
                    Text("Carbs: ${"%.1f".format(totalCarbs)} g")
                    Text("Fat: ${"%.1f".format(totalFat)} g")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { showAddFoodDialog = true }, modifier = Modifier.fillMaxWidth()) { Text("Add food") }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Foods", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (meals.isEmpty()) {
                Text("No foods for this date")
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(meals, key = { it.id }) { meal ->
                        MealCard(
                            meal = meal,
                            onEdit = {
                                editingMeal = meal
                                foodName = meal.foodName
                                grams = meal.grams.toString()
                            },
                            onDelete = {
                                scope.launch {
                                    try {
                                        RetrofitInstance.api.deleteMeal(meal.id, userId)
                                        loadMeals()
                                    } catch (e: Exception) {
                                        message = "Error: ${e.message}"
                                    }
                                }
                            }
                        )
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
            AddOrEditFoodDialog(
                title = "Add food",
                foodName = foodName,
                grams = grams,
                onFoodNameChange = { foodName = it },
                onGramsChange = { grams = it },
                onDismiss = {
                    showAddFoodDialog = false
                    foodName = ""
                    grams = ""
                },
                onSaveClick = {
                    scope.launch {
                        try {
                            RetrofitInstance.api.addMeal(
                                userId = userId,
                                date = selectedDate.toString(),
                                request = MealRequest(foodName, grams.toDoubleOrNull() ?: 0.0)
                            )
                            showAddFoodDialog = false
                            foodName = ""
                            grams = ""
                            loadMeals()
                        } catch (e: Exception) {
                            message = "Error: ${e.message}"
                        }
                    }
                }
            )
        }

        if (editingMeal != null) {
            AddOrEditFoodDialog(
                title = "Edit food",
                foodName = foodName,
                grams = grams,
                onFoodNameChange = { foodName = it },
                onGramsChange = { grams = it },
                onDismiss = {
                    editingMeal = null
                    foodName = ""
                    grams = ""
                },
                onSaveClick = {
                    scope.launch {
                        try {
                            RetrofitInstance.api.updateMeal(
                                mealId = editingMeal!!.id,
                                userId = userId,
                                request = MealRequest(foodName, grams.toDoubleOrNull() ?: 0.0)
                            )
                            editingMeal = null
                            foodName = ""
                            grams = ""
                            loadMeals()
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
fun MealCard(meal: MealItem, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = meal.foodName, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${"%.0f".format(meal.grams)} g")
            Text("${"%.1f".format(meal.calories)} kcal")
            Text("Protein: ${"%.1f".format(meal.protein)} g")
            Text("Carbs: ${"%.1f".format(meal.carbs)} g")
            Text("Fat: ${"%.1f".format(meal.fat)} g")

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onEdit) { Text("Edit") }
                OutlinedButton(onClick = onDelete) { Text("Remove") }
            }
        }
    }
}

@Composable
fun AddOrEditFoodDialog(
    title: String,
    foodName: String,
    grams: String,
    onFoodNameChange: (String) -> Unit,
    onGramsChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSaveClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
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
        confirmButton = { Button(onClick = onSaveClick) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}