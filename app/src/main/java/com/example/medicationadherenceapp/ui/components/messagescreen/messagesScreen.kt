package com.example.medicationadherenceapp.ui.components.messagescreen

//import android.os.Build
//import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.medicationadherenceapp.ui.viewmodel.MessagesViewModel

//import kotlin.to

data class MessageItem(
    val id: String,
    val senderName: String,
    val sentAt: ZonedDateTime,
    val preview: String,
    val isOpened: Boolean
)

@Composable
fun MessagesScreen(
    messages: List<MessageItem>,
    modifier: Modifier = Modifier,
    onMessageClick: (MessageItem) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(messages, key = { it.id }) { msg ->
            MessageCard(msg, onMessageClick)
        }
    }
}

@Composable
private fun MessageCard(
    msg: MessageItem,
    onMessageClick: (MessageItem) -> Unit
) {
    val (dateLabel, isToday) = formatTimestamp(msg.sentAt)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMessageClick(msg) },
        colors = CardDefaults.cardColors(
            containerColor = if (msg.isOpened)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    msg.senderName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (msg.isOpened) FontWeight.Normal else FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = msg.preview,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AssistChip(
                    onClick = { onMessageClick(msg) },
                    label = { Text(if (msg.isOpened) "Opened" else "Unread") },
                    colors = AssistChipDefaults.assistChipColors()
                )
                if (isToday) {
                    Spacer(Modifier.weight(1f))
                    Text(
                        "Today",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

/**
 * Returns a pair of (label, isToday):
 * - If message is today -> "HH:mm"
 * - Else -> "yyyy-MM-dd"
 */
private fun formatTimestamp(zdt: ZonedDateTime): Pair<String, Boolean> {
    val locale = Locale.getDefault()
    val zone: ZoneId = zdt.zone
    val todayLocal = LocalDate.now(zone)
    val isToday = zdt.toLocalDate().isEqual(todayLocal)

    val label = if (isToday) {
        DateTimeFormatter.ofPattern("HH:mm", locale).format(zdt)
    } else {
        DateTimeFormatter.ofPattern("yyyy-MM-dd", locale).format(zdt)
    }
    return label to isToday
}

@Composable
fun MessagesRoute(
    viewModel: MessagesViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()

    MessagesScreen(
        messages = messages,
        onMessageClick = { msg ->
            viewModel.markOpened(msg.id)
            // TODO: navigate to a message detail screen if you want
        }
    )
}

