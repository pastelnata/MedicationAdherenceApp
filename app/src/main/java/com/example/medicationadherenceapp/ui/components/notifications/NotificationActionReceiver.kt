package com.example.medicationadherenceapp.ui.components.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class YourBroadcastReceiver : BroadcastReceiver() {

    companion object {
        // Use a unique action string to avoid conflicts with other apps.
        const val ACTION_TAKEN = "com.yourapp.action.ACTION_TAKEN"
        const val EXTRA_NOTIFICATION_ID = "com.yourapp.extra.NOTIFICATION_ID"
        const val INVALID_NOTIFICATION_ID = -1
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Use 'when' for handling multiple actions in a scalable way.
        when (intent.action) {
            ACTION_TAKEN -> {
                val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, INVALID_NOTIFICATION_ID)

                // Check if the notificationId is valid before proceeding.
                if (notificationId != INVALID_NOTIFICATION_ID) {
                    // --- Action Logic ---
                    // In a real app, you would update your database or repository here
                    // to mark the medication as taken.
                    Log.d("NotificationAction", "Medication marked as taken for notification ID: $notificationId")

                    // Consider showing a Toast only if the app is in the foreground
                    // or use a Notification to provide feedback.
                    Toast.makeText(context, "Medication marked as taken!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.w("NotificationAction", "Invalid notification ID received.")
                }
            }
            // You can add more actions here in the future.
            // "com.yourapp.action.ANOTHER_ACTION" -> { /* ... */ }
        }
    }
}
