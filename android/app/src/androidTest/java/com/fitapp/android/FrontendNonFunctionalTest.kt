package com.fitapp.android

import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.fitapp.android.ui.theme.FitAppAndroidTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FrontendNonFunctionalTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun nr1_mainScreensShouldOpenUnderTwoSeconds() {
        val initialRenderStart = SystemClock.elapsedRealtime()

        composeRule.setContent {
            FitAppAndroidTheme {
                var selectedTab by remember { mutableStateOf(AppTab.CALORIES) }

                MainTabs(
                    userId = 1L,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    onLogout = {}
                )
            }
        }

        waitUntilVisible("Nutrition Dashboard")
        val caloriesTime = SystemClock.elapsedRealtime() - initialRenderStart
        println("NR-1 Calories screen load time: $caloriesTime ms")
        assertTrue("Calories screen took too long: $caloriesTime ms", caloriesTime < 2000)

        val workoutsStart = SystemClock.elapsedRealtime()
        composeRule.onNodeWithText("Workouts").performClick()
        waitUntilVisible("Workout Planner")
        val workoutsTime = SystemClock.elapsedRealtime() - workoutsStart
        println("NR-1 Workouts screen load time: $workoutsTime ms")
        assertTrue("Workouts screen took too long: $workoutsTime ms", workoutsTime < 2000)

        val profileStart = SystemClock.elapsedRealtime()
        composeRule.onNodeWithText("Profile").performClick()
        waitUntilVisible("Profile Settings")
        val profileTime = SystemClock.elapsedRealtime() - profileStart
        println("NR-1 Profile screen load time: $profileTime ms")
        assertTrue("Profile screen took too long: $profileTime ms", profileTime < 2000)
    }

    @Test
    fun sr1_sr2_appScreensShouldRenderOnAndroidDevice() {
        composeRule.setContent {
            FitAppAndroidTheme {
                var selectedTab by remember { mutableStateOf(AppTab.CALORIES) }

                MainTabs(
                    userId = 1L,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    onLogout = {}
                )
            }
        }

        composeRule.onNodeWithText("Nutrition Dashboard").assertIsDisplayed()
        composeRule.onNodeWithText("Calories").assertIsDisplayed()
        composeRule.onNodeWithText("Workouts").assertIsDisplayed()
        composeRule.onNodeWithText("Profile").assertIsDisplayed()

        composeRule.onNodeWithText("Workouts").performClick()
        composeRule.onNodeWithText("Workout Planner").assertIsDisplayed()
        composeRule.onNodeWithText("Create workout").assertIsDisplayed()
        composeRule.onNodeWithText("AI plan").assertIsDisplayed()

        composeRule.onNodeWithText("Profile").performClick()
        composeRule.onNodeWithText("Profile Settings").assertIsDisplayed()
        composeRule.onNodeWithText("Save details").assertIsDisplayed()
        composeRule.onNodeWithText("Log out").assertIsDisplayed()
    }

    @Test
    fun pr1_pr2_mainFunctionsShouldBeClearAndReachable() {
        composeRule.setContent {
            FitAppAndroidTheme {
                var selectedTab by remember { mutableStateOf(AppTab.CALORIES) }

                MainTabs(
                    userId = 1L,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    onLogout = {}
                )
            }
        }

        composeRule.onNodeWithText("Nutrition Dashboard").assertIsDisplayed()
        composeRule.onNodeWithText("Add food entry").assertIsDisplayed()

        composeRule.onNodeWithText("Add food entry").performClick()
        composeRule.onNodeWithText("Add food").assertIsDisplayed()
        composeRule.onNodeWithText("Food name").assertIsDisplayed()
        composeRule.onNodeWithText("Grams").assertIsDisplayed()
        composeRule.onNodeWithText("Cancel").performClick()

        composeRule.onNodeWithText("Workouts").performClick()
        composeRule.onNodeWithText("Workout Planner").assertIsDisplayed()

        composeRule.onNodeWithText("Create workout").performClick()
        composeRule.onNodeWithText("Workout name").assertIsDisplayed()
        composeRule.onNodeWithText("Muscle group").assertIsDisplayed()
        composeRule.onNodeWithText("Exercise").assertIsDisplayed()
        composeRule.onNodeWithText("Cancel").performClick()

        composeRule.onNodeWithText("Profile").performClick()
        composeRule.onNodeWithText("Profile Settings").assertIsDisplayed()
        composeRule.onNodeWithText("Age").assertIsDisplayed()
        composeRule.onNodeWithText("Weight (kg)").assertIsDisplayed()
        composeRule.onNodeWithText("Save details").assertIsDisplayed()
    }

    @Test
    fun pr4_navigationAndMainStyleShouldBeConsistent() {
        composeRule.setContent {
            FitAppAndroidTheme {
                var selectedTab by remember { mutableStateOf(AppTab.CALORIES) }

                MainTabs(
                    userId = 1L,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    onLogout = {}
                )
            }
        }

        composeRule.onNodeWithText("Calories").assertIsDisplayed()
        composeRule.onNodeWithText("Workouts").assertIsDisplayed()
        composeRule.onNodeWithText("Profile").assertIsDisplayed()

        composeRule.onNodeWithText("Workouts").performClick()
        composeRule.onNodeWithText("Calories").assertIsDisplayed()
        composeRule.onNodeWithText("Workouts").assertIsDisplayed()
        composeRule.onNodeWithText("Profile").assertIsDisplayed()

        composeRule.onNodeWithText("Profile").performClick()
        composeRule.onNodeWithText("Calories").assertIsDisplayed()
        composeRule.onNodeWithText("Workouts").assertIsDisplayed()
        composeRule.onNodeWithText("Profile").assertIsDisplayed()
    }

    @Test
    fun tkr1_tkr2_nutritionScreenShouldShowCaloriesAndGrams() {
        composeRule.setContent {
            FitAppAndroidTheme {
                MainTabs(
                    userId = 1L,
                    selectedTab = AppTab.CALORIES,
                    onTabSelected = {},
                    onLogout = {}
                )
            }
        }

        composeRule.onNodeWithText("Nutrition Dashboard").assertIsDisplayed()
        composeRule.onNodeWithText("Total calories").assertIsDisplayed()
        composeRule.onNodeWithText("0.0 kcal").assertIsDisplayed()
        composeRule.onNodeWithText("Protein: 0.0 g").assertIsDisplayed()
        composeRule.onNodeWithText("Carbs: 0.0 g").assertIsDisplayed()
        composeRule.onNodeWithText("Fat: 0.0 g").assertIsDisplayed()

        composeRule.onNodeWithText("Add food entry").performClick()
        composeRule.onNodeWithText("Food name").assertIsDisplayed()
        composeRule.onNodeWithText("Grams").assertIsDisplayed()
    }

    private fun waitUntilVisible(text: String) {
        composeRule.waitUntil(timeoutMillis = 2000) {
            try {
                composeRule.onNodeWithText(text).assertIsDisplayed()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        composeRule.onNodeWithText(text).assertIsDisplayed()
    }
}