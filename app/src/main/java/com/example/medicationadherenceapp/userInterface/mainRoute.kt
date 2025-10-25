package com.example.medicationadherenceapp.userInterface

import androidx.compose.runtime.*
import com.example.medicationadherenceapp.userInterface.DoseUi

@Composable
fun MainRoute() {
    var urgent by remember {
        mutableStateOf(
            listOf(
                DoseUi("1","Metformin","500 mg","Twice daily", minutesOverdue = 60, instructions = "Take with meals")
            )
        )
    }
    var today by remember {
        mutableStateOf(
            listOf(
                DoseUi("2","Vitamin D","40 µg", timeLabel = "09:00"),
                DoseUi("3","Ibuprofen","200 mg", timeLabel = "14:00")
            )
        )
    }
    var taken by remember { mutableIntStateOf(0) }

    val confirmTaken: (DoseUi) -> Unit = { d ->
        // remove from whichever list contains it
        if (urgent.any { it.id == d.id }) urgent = urgent.filterNot { it.id == d.id }
        if (today.any { it.id == d.id }) today = today.filterNot { it.id == d.id }
        taken += 1
        // TODO: persist "taken" in your repository / DB
    }

    val skip: (DoseUi) -> Unit = { d ->
        urgent = urgent.filterNot { it.id == d.id }
        today = today.filterNot { it.id == d.id }
        // taken unchanged for skip
        // TODO: maybe snooze instead of removing
    }

    MainPage(
        urgentDoses = urgent,
        todayDoses = today,
        onConfirmTaken = confirmTaken,
        onSkip = skip,
        takenCount = taken
    )
}


