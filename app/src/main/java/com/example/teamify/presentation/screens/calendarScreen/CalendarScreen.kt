package com.example.teamify.presentation.screens.calendarScreen

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teamify.domain.model.Event
import com.example.teamify.ui.theme.AppTheme
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun CalendarScreen(
    state: CalendarUiState,
    onDayClick: (LocalDate) -> Unit, // Open "Add Event" screen
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val currentMonth = remember { YearMonth.now() }
    val startMonth = currentMonth.minusMonths(100)
    val endMonth = currentMonth.plusMonths(100)
    val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY)

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    val scope = rememberCoroutineScope()

    Box(){

        Column(Modifier.fillMaxSize()) {

            // Month header
            MonthHeader(
                yearMonth = calendarState.firstVisibleMonth.yearMonth,
                onPrev = {
                    scope.launch {
                        calendarState.scrollToMonth(calendarState.firstVisibleMonth.yearMonth.minusMonths(1))
                    }
                },
                onNext = {
                    scope.launch {
                        calendarState.scrollToMonth(calendarState.firstVisibleMonth.yearMonth.plusMonths(1))
                    }
                }
            )

            // Days of week row
            DaysOfWeekTitle(daysOfWeek)

            // Horizontal calendar
            HorizontalCalendar(
                state = calendarState,
                dayContent = { day ->
                    val eventCount = state.events.count { it.date == day.date }
                    Day(
                        day = day,
                        onDayClick = { selectedDate = it },
                        eventCount = eventCount,
                        isSelected = selectedDate == day.date
                    )
                }
            )

            Spacer(Modifier.height(16.dp))

            // Events section with scrollable LazyColumn
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state.events.filter { it.date == selectedDate }.isEmpty())
                            "No events this day."
                        else "Events:",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Scrollable list of events
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(state.events.filter { it.date == selectedDate }) { event ->
                        ExpandableEventItem(event)
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { onDayClick(selectedDate) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Event",
                tint = Color.White
            )
        }

    }


}
@Composable
fun MonthHeader(
    yearMonth: YearMonth,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = onPrev) {
            Icon(Icons.Default.ArrowBackIosNew, contentDescription = null)
        }

        Text(
            text = "${yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${yearMonth.year}",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
        )

        IconButton(onClick = onNext) {
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null)
        }
    }
}
@Composable
fun ExpandableEventItem(event: Event) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left accent bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary)
            )

            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            event.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            event.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                AnimatedVisibility(visible = expanded) {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        event.time?.let {
                            Text("Time: $it", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        if (event.description.isNotEmpty()) {
                            Text(event.description, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            )
        }
    }
}

@Composable
fun Day(
    day: CalendarDay,
    isSelected: Boolean,
    eventCount: Int,
    onDayClick: (LocalDate) -> Unit
) {
    val isCurrentMonth = day.position == DayPosition.MonthDate
    val bgColor =
        if (isSelected) MaterialTheme.colorScheme.primaryContainer
        else Color.Transparent

    val textColor =
        if (!isCurrentMonth) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
        else MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clickable { onDayClick(day.date) }
            .background(bgColor, shape = MaterialTheme.shapes.small),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor
        )

        if (eventCount > 0) {
            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                repeat(eventCount.coerceAtMost(3)) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.extraSmall
                            )
                    )
                }
            }
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
fun CalendarScreenPreview() {
    AppTheme {
        Surface(tonalElevation = 5.dp) {
            CalendarScreen(
                state = CalendarUiState(
                    events = listOf(
                        Event(
                            id = "1",
                            date = LocalDate.now(),
                            title = "Team Meeting"
                        ),
                        Event(
                            id = "2",
                            date = LocalDate.now().plusDays(2),
                            title = "Project Deadline"
                        ),
                        Event(
                            id = "3",
                            date = LocalDate.now().plusDays(5),
                            title = "Client Call"
                        ),
                    )
                ),
                onDayClick = {},
            )
        }
    }

}

@Preview
@Composable
fun dayPreview() {
    AppTheme {
        Day(
            day = CalendarDay(
                date = LocalDate.now(),
                position = DayPosition.MonthDate
            ),
            onDayClick = {},
            eventCount = 3,
            isSelected = true
        )
    }
}
