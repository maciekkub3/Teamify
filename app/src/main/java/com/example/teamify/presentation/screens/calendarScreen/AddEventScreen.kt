package com.example.teamify.presentation.screens.calendarScreen

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teamify.presentation.common.TopBar
import com.example.teamify.ui.theme.AppTheme
import java.time.LocalDateTime

@SuppressLint("SimpleDateFormat", "DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    state: AddEventUiState,
    event: AddEventUiEvent?,
    onDateSelected: (LocalDateTime) -> Unit,
    onSave: () -> Unit,
    onTimeSelected: (LocalDateTime?) -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onNavigateBack: () -> Unit
) {

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()
    var showDateDialog by remember { mutableStateOf(false) }
    var showTimeDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(event) {
        event?.let {
            when (it) {
                is AddEventUiEvent.ShowError -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
                is AddEventUiEvent.Success -> {
                    Toast.makeText(context, "Event added successfully", Toast.LENGTH_LONG).show()
                    onNavigateBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                "Add Event",
                onBackClick = { onNavigateBack() }
            )
        },
        bottomBar = {
            Button(
                onClick = onSave,
                enabled = state.eventTitle.isNotBlank() && state.eventDescription.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Save")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.eventTitle,
                onValueChange = { onTitleChange(it) },
                label = { Text("Event Title") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDateDialog = true }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Date: ${state.eventDate.year}-${state.eventDate.monthValue}-${state.eventDate.dayOfMonth}")
                Icon(Icons.Default.CalendarToday, contentDescription = "Pick date")
            }

            if (showDateDialog) {
                DatePickerDialog(
                    onDismissRequest = { showDateDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            onDateSelected(
                                LocalDateTime.of(
                                    datePickerState.selectedDateMillis?.let {
                                        java.time.Instant.ofEpochMilli(it)
                                            .atZone(java.time.ZoneId.systemDefault())
                                            .toLocalDate()
                                    },
                                    LocalDateTime.now().toLocalTime()
                                )
                            )
                            showDateDialog = false
                        }) { Text("OK") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTimeDialog = true }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if(state.eventTime != null)
                        "Time: ${state.eventTime.hour}:${state.eventTime.minute}"
                    else
                        "Time: Not Set"
                )
                Icon(Icons.Default.AccessTime, contentDescription = "Pick time")
            }


            if (showTimeDialog) {
                TimePickerDialog(
                    onDismissRequest = { showTimeDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            val pickedTime = LocalDateTime.of(
                                state.eventDate.toLocalDate(), // use current selected date
                                java.time.LocalTime.of(timePickerState.hour, timePickerState.minute)
                            )
                            onTimeSelected(pickedTime)
                            showTimeDialog = false
                        }) { Text("OK") }
                    },
                    title = { Text("Select Time") }
                ) {
                    TimePicker(state = timePickerState)
                }
            }



            OutlinedTextField(
                value = state.eventDescription,
                onValueChange = { onDescriptionChange(it) },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)

            )
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Normal mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark mode"
)
@Composable
fun AddEventScreenPreview() {
    AppTheme {
        Surface(tonalElevation = 5.dp) {
            AddEventScreen(
                state = AddEventUiState(
                    eventTitle = "Team Meeting",
                    eventDescription = "Discuss project updates",
                    eventDate = LocalDateTime.now(), // Example timestamp
                ),
                onDateSelected = {},
                onSave = {},
                onTimeSelected = {},
                onTitleChange = {},
                onDescriptionChange = {},
                event = null,
                onNavigateBack = {}
            )
        }
    }
}
