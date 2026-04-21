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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fitapp.android.api.RetrofitInstance
import com.fitapp.android.model.LoginRequest
import com.fitapp.android.model.GenerateWorkoutRequest
import com.fitapp.android.model.ExerciseLibraryResponse
import com.fitapp.android.model.MealItem
import com.fitapp.android.model.MealRequest
import com.fitapp.android.model.ProfileRequest
import com.fitapp.android.model.ProfileResponse
import com.fitapp.android.model.RegisterRequest
import com.fitapp.android.model.WorkoutExercisePayload
import com.fitapp.android.model.WorkoutPayload
import com.fitapp.android.model.WorkoutResponse
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

enum class AppTab(val label: String) {
    CALORIES("Calories"),
    WORKOUTS("Workouts"),
    PROFILE("Profile")
}

@Composable
fun App() {
    var loggedInUserId by remember { mutableStateOf<Long?>(null) }
    var selectedTab by remember { mutableStateOf(AppTab.CALORIES) }

    if (loggedInUserId != null) {
        MainTabs(
            userId = loggedInUserId!!,
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            onLogout = {
                loggedInUserId = null
                selectedTab = AppTab.CALORIES
            }
        )
    } else {
        AuthScreen(onLoginSuccess = { userId -> loggedInUserId = userId })
    }
}

@Composable
fun MainTabs(
    userId: Long,
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    onLogout: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                AppTab.CALORIES -> CaloriesScreen(userId = userId)
                AppTab.WORKOUTS -> WorkoutsScreen(userId = userId)
                AppTab.PROFILE -> ProfileScreen(userId = userId, onLogout = onLogout)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                if (isSelected) {
                    Button(onClick = { onTabSelected(tab) }, modifier = Modifier.weight(1f)) {
                        Text(tab.label)
                    }
                } else {
                    OutlinedButton(onClick = { onTabSelected(tab) }, modifier = Modifier.weight(1f)) {
                        Text(tab.label)
                    }
                }
            }
        }
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
fun CaloriesScreen(userId: Long) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showAddFoodDialog by remember { mutableStateOf(false) }
    var editingMeal by remember { mutableStateOf<MealItem?>(null) }

    var foodName by remember { mutableStateOf("") }
    var grams by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    var meals by remember { mutableStateOf(emptyList<MealItem>()) }
    var profile by remember { mutableStateOf<ProfileResponse?>(null) }
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

    fun loadProfile() {
        scope.launch {
            try {
                profile = RetrofitInstance.api.getProfile(userId)
            } catch (_: Exception) {
                // Optional for this screen.
            }
        }
    }

    LaunchedEffect(selectedDate) { loadMeals() }
    LaunchedEffect(Unit) { loadProfile() }

    val totalCalories = meals.sumOf { it.calories }
    val totalProtein = meals.sumOf { it.protein }
    val totalCarbs = meals.sumOf { it.carbs }
    val totalFat = meals.sumOf { it.fat }
    val recommendedCalories = profile?.recommendedCalories

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(text = "Calories", style = MaterialTheme.typography.headlineMedium)
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
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Daily target", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (recommendedCalories == null) {
                        Text("Set up your profile to get a target calorie goal.")
                    } else {
                        val progress = (totalCalories / recommendedCalories).coerceIn(0.0, 1.0).toFloat()
                        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("${"%.0f".format(totalCalories)} / ${"%.0f".format(recommendedCalories)} kcal")
                    }
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
fun WorkoutsScreen(userId: Long) {
    val scope = rememberCoroutineScope()

    var workouts by remember { mutableStateOf(emptyList<WorkoutDraft>()) }
    var showWorkoutEditor by remember { mutableStateOf(false) }
    var editingWorkoutId by remember { mutableStateOf<Long?>(null) }
    var workoutName by remember { mutableStateOf("") }
    val workoutExercises = remember { mutableStateListOf<WorkoutExerciseDraft>() }

    var exerciseLibrary by remember { mutableStateOf<ExerciseLibraryResponse?>(null) }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedExercise by remember { mutableStateOf("") }

    var showAiGenerator by remember { mutableStateOf(false) }
    var aiLevel by remember { mutableStateOf("BEGINNER") }
    var aiDuration by remember { mutableStateOf("45") }
    var aiDays by remember { mutableStateOf("3") }
    var message by remember { mutableStateOf("") }

    fun loadExerciseLibrary() {
        scope.launch {
            try {
                exerciseLibrary = RetrofitInstance.api.getExerciseLibrary()
                val firstCategory = exerciseLibrary?.categories?.keys?.firstOrNull().orEmpty()
                selectedCategory = firstCategory
                selectedExercise = exerciseLibrary?.categories?.get(firstCategory)?.firstOrNull().orEmpty()
            } catch (e: Exception) {
                message = "Error loading exercise list: ${e.message}"
            }
        }
    }

    fun loadWorkouts() {
        scope.launch {
            try {
                workouts = RetrofitInstance.api.getWorkouts(userId).map { it.toDraft() }
                message = ""
            } catch (e: Exception) {
                message = "Error loading workouts: ${e.message}"
            }
        }
    }

    fun startCreateWorkout() {
        editingWorkoutId = null
        workoutName = ""
        workoutExercises.clear()
        showWorkoutEditor = true
    }

    fun startEditWorkout(workout: WorkoutDraft) {
        editingWorkoutId = workout.id
        workoutName = workout.name
        workoutExercises.clear()
        workoutExercises.addAll(workout.exercises.map { it.copy() })
        showWorkoutEditor = true
    }

    LaunchedEffect(Unit) {
        loadExerciseLibrary()
        loadWorkouts()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Workouts", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { startCreateWorkout() }, modifier = Modifier.weight(1f)) {
                Text("Create workout")
            }
            OutlinedButton(onClick = { showAiGenerator = true }, modifier = Modifier.weight(1f)) {
                Text("AI Generate")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (workouts.isEmpty()) {
            Text("No workouts yet. Create one to get started.")
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(workouts, key = { it.id }) { workout ->
                    WorkoutCard(
                        workout = workout,
                        onEdit = { startEditWorkout(workout) },
                        onDelete = {
                            scope.launch {
                                try {
                                    RetrofitInstance.api.deleteWorkout(workout.id, userId)
                                    loadWorkouts()
                                } catch (e: Exception) {
                                    message = "Error deleting workout: ${e.message}"
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

    if (showWorkoutEditor) {
        WorkoutEditorDialog(
            title = if (editingWorkoutId == null) "Create workout" else "Edit workout",
            workoutName = workoutName,
            workoutExercises = workoutExercises,
            exerciseLibrary = exerciseLibrary,
            selectedCategory = selectedCategory,
            selectedExercise = selectedExercise,
            onWorkoutNameChange = { workoutName = it },
            onSelectedCategoryChange = { category ->
                selectedCategory = category
                selectedExercise = exerciseLibrary?.categories?.get(category)?.firstOrNull().orEmpty()
            },
            onSelectedExerciseChange = { selectedExercise = it },
            onAddExercise = {
                if (selectedExercise.isNotBlank()) {
                    workoutExercises.add(WorkoutExerciseDraft(name = selectedExercise, weightKg = "", reps = ""))
                }
            },
            onExerciseWeightChange = { index, value ->
                workoutExercises[index] = workoutExercises[index].copy(weightKg = value)
            },
            onExerciseRepsChange = { index, value ->
                workoutExercises[index] = workoutExercises[index].copy(reps = value)
            },
            onRemoveExercise = { index -> workoutExercises.removeAt(index) },
            onDismiss = { showWorkoutEditor = false },
            onSave = {
                scope.launch {
                    try {
                        val payload = WorkoutPayload(
                            name = workoutName.trim().ifBlank { "Untitled Workout" },
                            exercises = workoutExercises.map { WorkoutExercisePayload(it.name, it.reps, it.weightKg) }
                        )
                        if (editingWorkoutId == null) {
                            RetrofitInstance.api.createWorkout(userId = userId, request = payload)
                        } else {
                            RetrofitInstance.api.updateWorkout(
                                workoutId = editingWorkoutId!!,
                                userId = userId,
                                request = payload
                            )
                        }
                        showWorkoutEditor = false
                        loadWorkouts()
                    } catch (e: Exception) {
                        message = "Error saving workout: ${e.message}"
                    }
                }
            }
        )
    }

    if (showAiGenerator) {
        AiGeneratorDialog(
            level = aiLevel,
            durationMinutes = aiDuration,
            trainingDays = aiDays,
            onLevelChange = { aiLevel = it },
            onDurationChange = { aiDuration = it },
            onTrainingDaysChange = { aiDays = it },
            onDismiss = { showAiGenerator = false },
            onGenerate = {
                scope.launch {
                    try {
                        val generated = RetrofitInstance.api.generateWorkoutPlan(
                            GenerateWorkoutRequest(
                                level = aiLevel,
                                durationMinutes = aiDuration.toIntOrNull() ?: 45,
                                trainingDaysPerWeek = aiDays.toIntOrNull() ?: 3
                            )
                        )

                        val payload = WorkoutPayload(
                            name = generated.workoutName,
                            exercises = generated.exercises.map {
                                WorkoutExercisePayload(
                                    name = it.name,
                                    reps = it.reps,
                                    weightKg = it.targetWeightKg?.toString().orEmpty()
                                )
                            }
                        )
                        RetrofitInstance.api.createWorkout(userId = userId, request = payload)
                        showAiGenerator = false
                        message = "AI workout added to your account."
                        loadWorkouts()
                    } catch (e: Exception) {
                        message = "Error generating AI workout: ${e.message}"
                    }
                }
            }
        )
    }
}

data class WorkoutDraft(
    val id: Long,
    val name: String,
    val exercises: List<WorkoutExerciseDraft>
)

data class WorkoutExerciseDraft(
    val name: String,
    val weightKg: String,
    val reps: String
)

private fun WorkoutResponse.toDraft(): WorkoutDraft {
    return WorkoutDraft(
        id = id,
        name = name,
        exercises = exercises.map { WorkoutExerciseDraft(name = it.name, weightKg = it.weightKg, reps = it.reps) }
    )
}

@Composable
fun WorkoutCard(workout: WorkoutDraft, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(workout.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            workout.exercises.forEach { exercise ->
                Text("• ${exercise.name} — ${exercise.reps.ifBlank { "reps not set" }} reps, ${exercise.weightKg.ifBlank { "weight not set" }} kg")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onEdit) { Text("Edit") }
                OutlinedButton(onClick = onDelete) { Text("Remove") }
            }
        }
    }
}

@Composable
fun WorkoutEditorDialog(
    title: String,
    workoutName: String,
    workoutExercises: List<WorkoutExerciseDraft>,
    exerciseLibrary: ExerciseLibraryResponse?,
    selectedCategory: String,
    selectedExercise: String,
    onWorkoutNameChange: (String) -> Unit,
    onSelectedCategoryChange: (String) -> Unit,
    onSelectedExerciseChange: (String) -> Unit,
    onAddExercise: () -> Unit,
    onExerciseWeightChange: (Int, String) -> Unit,
    onExerciseRepsChange: (Int, String) -> Unit,
    onRemoveExercise: (Int) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val categories = exerciseLibrary?.categories?.keys?.toList().orEmpty()
    val exercisesForCategory = exerciseLibrary?.categories?.get(selectedCategory).orEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            LazyColumn {
                item {
                    TextField(
                        value = workoutName,
                        onValueChange = onWorkoutNameChange,
                        label = { Text("Workout name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    DropdownSelector(
                        label = "Muscle group",
                        selectedValue = selectedCategory.ifBlank { "Choose group" },
                        options = categories,
                        onSelected = onSelectedCategoryChange
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DropdownSelector(
                        label = "Exercise",
                        selectedValue = selectedExercise.ifBlank { "Choose exercise" },
                        options = exercisesForCategory,
                        onSelected = onSelectedExerciseChange
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = onAddExercise, modifier = Modifier.fillMaxWidth()) {
                        Text("Add exercise")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Exercises in workout", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(6.dp))
                }

                items(workoutExercises.size) { index ->
                    val exercise = workoutExercises[index]
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(exercise.name, style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(6.dp))
                            TextField(
                                value = exercise.weightKg,
                                onValueChange = { onExerciseWeightChange(index, it) },
                                label = { Text("Weight (kg)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            TextField(
                                value = exercise.reps,
                                onValueChange = { onExerciseRepsChange(index, it) },
                                label = { Text("Rep count") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedButton(onClick = { onRemoveExercise(index) }) {
                                Text("Remove")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        confirmButton = { Button(onClick = onSave) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AiGeneratorDialog(
    level: String,
    durationMinutes: String,
    trainingDays: String,
    onLevelChange: (String) -> Unit,
    onDurationChange: (String) -> Unit,
    onTrainingDaysChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onGenerate: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("AI Workout Generator") },
        text = {
            Column {
                DropdownSelector(
                    label = "Experience level",
                    selectedValue = level,
                    options = listOf("BEGINNER", "INTERMEDIATE", "ADVANCED"),
                    onSelected = onLevelChange
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = durationMinutes,
                    onValueChange = onDurationChange,
                    label = { Text("Workout length (minutes)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = trainingDays,
                    onValueChange = onTrainingDaysChange,
                    label = { Text("Training days per week") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = { Button(onClick = onGenerate) { Text("Generate") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun ProfileScreen(userId: Long, onLogout: () -> Unit) {
    val scope = rememberCoroutineScope()

    val activityOptions = listOf(
        "Mostly sedentary (little or no exercise)",
        "Lightly active (1-2 workouts/week)",
        "Moderately active (3-4 workouts/week)",
        "Very active (5-7 workouts/week)"
    )
    val goalOptions = listOf("Maintain weight", "Gain weight", "Lose weight")

    var profile by remember { mutableStateOf<ProfileResponse?>(null) }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var selectedActivity by remember { mutableStateOf(activityOptions[0]) }
    var selectedGoal by remember { mutableStateOf(goalOptions[0]) }
    var message by remember { mutableStateOf("") }

    fun loadProfile() {
        scope.launch {
            try {
                val response = RetrofitInstance.api.getProfile(userId)
                profile = response
                age = response.age?.toString() ?: ""
                weight = response.weightKg?.toString() ?: ""
                selectedActivity = response.activityLevel ?: activityOptions[0]
                selectedGoal = response.goal ?: goalOptions[0]
            } catch (e: Exception) {
                message = "Error: ${e.message}"
            }
        }
    }

    LaunchedEffect(Unit) { loadProfile() }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Text("Name: ${profile?.username ?: "-"}")
        Text("Email: ${profile?.email ?: "-"}")

        Spacer(modifier = Modifier.height(16.dp))
        Text("Your details", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        TextField(value = age, onValueChange = { age = it }, label = { Text("Age") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (kg)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        DropdownSelector(
            label = "Activity level",
            selectedValue = selectedActivity,
            options = activityOptions,
            onSelected = { selectedActivity = it }
        )
        Spacer(modifier = Modifier.height(8.dp))
        DropdownSelector(
            label = "Goal",
            selectedValue = selectedGoal,
            options = goalOptions,
            onSelected = { selectedGoal = it }
        )

        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                scope.launch {
                    try {
                        profile = RetrofitInstance.api.updateProfile(
                            userId = userId,
                            request = ProfileRequest(
                                age = age.toIntOrNull(),
                                weightKg = weight.toDoubleOrNull(),
                                activityLevel = selectedActivity,
                                goal = selectedGoal
                            )
                        )
                        message = "Profile updated"
                    } catch (e: Exception) {
                        message = "Error: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save details")
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Recommended calories: ${profile?.recommendedCalories?.let { "%.0f".format(it) } ?: "Not calculated yet"}"
        )

        if (message.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(message)
        }

        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Text("Log out")
        }
    }
}

@Composable
fun DropdownSelector(
    label: String,
    selectedValue: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label)
        Spacer(modifier = Modifier.height(4.dp))
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selectedValue)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelected(option)
                            expanded = false
                        }
                    )
                }
            }
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