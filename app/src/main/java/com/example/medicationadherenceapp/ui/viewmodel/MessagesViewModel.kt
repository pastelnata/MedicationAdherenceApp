package com.example.medicationadherenceapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.medicationadherenceapp.ui.components.messagescreen.MessageItem
import java.time.ZonedDateTime

@HiltViewModel
class MessagesViewModel @Inject constructor(
    // later: inject a repository here
) : ViewModel() {

    private val _messages = MutableStateFlow<List<MessageItem>>(emptyList())
    val messages: StateFlow<List<MessageItem>> = _messages

    init {
        // TODO: replace with real data
        _messages.value = listOf(
            MessageItem(
                id = "m1",
                senderName = "Dr. Jensen",
                sentAt = ZonedDateTime.now().minusHours(2),
                preview = "Remember to bring your medication list to our next appointment.",
                isOpened = false
            ),
            MessageItem(
                id = "m2",
                senderName = "Nurse",
                sentAt = ZonedDateTime.now().minusDays(1),
                preview = "Your test results are available in the portal.",
                isOpened = true
            )
        )
    }

    fun markOpened(id: String) {
        _messages.value = _messages.value.map {
            if (it.id == id) it.copy(isOpened = true) else it
        }
    }
}
