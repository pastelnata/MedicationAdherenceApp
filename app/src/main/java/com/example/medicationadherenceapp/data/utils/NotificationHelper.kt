package com.example.medicationadherenceapp.data.utils
/**
import androidx.privacysandbox.tools.core.generator.build

android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.medicationadherenceapp.MainActivity
import com.example.medicationadherenceapp.NotificationActionReceiver
import com.example.medicationadherenceapp.R

/**
 * A helper class to manage notification channels and create notifications.
 *
 * This class centralizes notification logic, such as channel creation
 * and the construction of specific notifications like medication reminders.
 *
 * param context The application context, used to access system services.
 */
class NotificationHelper(private val context: Context) {

    // The NotificationManager system service, initialized lazily for efficiency.
    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    /**
     * Companion object to hold constants that are used both within this class
     * and by other components of the app (like the BroadcastReceiver).
     */
    companion object {
        // Channel constants
        const val REMINDER_CHANNEL_ID = "medication_reminder_channel"
        private const val REMINDER_CHANNEL_NAME = "Medication Reminders"
        private const val REMINDER_CHANNEL_DESCRIPTION = "Notifications to remind you to take your medication"

        // Action and extra constants, prefixed with package name for uniqueness.
        const val ACTION_TAKEN = "com.example.medicationadherenceapp.ACTION_TAKEN"
        const val EXTRA_NOTIFICATION_ID = "com.example.medicationadherenceapp.EXTRA_NOTIFICATION_ID"
    }

    /**
     * Creates the notification channel required for Android 8.0 (API 26) and above.
     * It's safe to call this multiple times; creating an existing channel performs no operation.
     * This should be called on app startup, for instance, in your Application class.
     */
        fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // High-Priority Reminder Channel
        val reminderChannel = NotificationChannel(
        REMINDER_CHANNEL_ID,
        REMINDER_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH
        ).apply {
        description = REMINDER_CHANNEL_DESCRIPTION
        enableLights(true)
        lightColor = android.graphics.Color.BLUE
        enableVibration(true)
        }

        // Default-Priority General Channel
        val generalChannel = NotificationChannel(
        GENERAL_CHANNEL_ID,
        GENERAL_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_DEFAULT // Lower priority
        ).apply {
        description = GENERAL_CHANNEL_DESCRIPTION
        }

        notificationManager.createNotificationChannel(reminderChannel)
        notificationManager.createNotificationChannel(generalChannel)
        }
        }

    /**
     * Builds and displays a medication reminder notification.
     *
     * param title The title of the notification (e.g., "Medication Reminder").
     * param content The main text of the notification (e.g., "It's time to take your Paracetamol.").
     * param notificationId A unique ID for this notification, used to update or cancel it.
     */
    fun showMedicationReminderNotification(title: String, content: String, notificationId: Int) {
        // Create the necessary PendingIntents for user interaction.
        val openAppPendingIntent = createOpenAppPendingIntent()
        val takenActionPendingIntent = createTakenActionPendingIntent(notificationId)

        // Build the notification with its content, intent, and actions.
        val builder = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // IMPORTANT: Ensure this drawable exists.
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // For older Android versions.
            .setContentIntent(openAppPendingIntent) // Set the action for tapping the notification body.
            .setAutoCancel(true) // Automatically dismisses the notification when tapped.
            .setOnlyAlertOnce(true) // Prevents sound/vibration on notification updates.
            .addAction(
                R.drawable.ic_check_circle, // Icon for the action button.
                "Mark as Taken",             // Text for the action button.
                takenActionPendingIntent   // The PendingIntent to fire on click.
            )

        // --- Show the notification ---
        notificationManager.notify(notificationId, builder.build())
    }

    /**
     * Creates a PendingIntent that opens the MainActivity.
     * This is used for the main notification tap action.
     * It is IMMUTABLE for security, as its internal Intent never changes.
     */
    private fun createOpenAppPendingIntent(): PendingIntent {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            context,
            0, // General request code for this type of intent.
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Creates a PendingIntent for the "Mark as Taken" notification action.
     * This intent will be handled by the NotificationActionReceiver.
     * It is MUTABLE because its extras (the notification ID) must be updated for each notification.
     *
     * param notificationId The ID of the notification, to be passed to the receiver.
     */
    private fun createTakenActionPendingIntent(notificationId: Int): PendingIntent {
        val takenIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_TAKEN
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }

        return PendingIntent.getBroadcast(
            context,
            notificationId, // Using the notificationId as the request code ensures uniqueness.
            takenIntent,
            // FLAG_UPDATE_CURRENT ensures the extra data is updated for each new notification.
            // FLAG_MUTABLE is required to use FLAG_UPDATE_CURRENT.
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }
}
 */