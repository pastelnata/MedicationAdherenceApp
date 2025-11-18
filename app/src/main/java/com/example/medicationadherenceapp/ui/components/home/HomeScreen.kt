package com.example.medicationadherenceapp.ui.components.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import comp.example.medicationadherenceapp.data.Medication

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel()
) {
    val medications by homeViewModel.medications.collectAsState()
    var searchQuery by rememberSaveable { mutableStateOf("") }

    Scaffold {
        Column(modifier = Modifier.padding(it)) {
            SearchBar(searchQuery = searchQuery, onQueryChange = { searchQuery = it })

            LazyColumn {
                items(medications.filter { it.name.contains(searchQuery, ignoreCase = true) }) { medication ->
                    MedicationItem(medication = medication, onClick = {
                        Log.d("HomeScreen", "Clicked on ${medication.name}")
                    })
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onQueryChange,
        label = { Text("Search Medications") },
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}

@Composable
fun MedicationItem(
    medication: Medication,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = medication.name,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    )
}
