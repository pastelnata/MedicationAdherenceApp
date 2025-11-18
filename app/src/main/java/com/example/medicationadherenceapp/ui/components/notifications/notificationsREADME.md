🔔 Notifications in the Medication Adherence App

This project implements Android notifications following modern best practices and the concepts covered in the lecture materials
(NotificationChannels, PendingIntent behavior, background execution rules, and handling user actions).
The notification system is structured into three core components:
+ NotificationHelper.kt
+ MyApplication.kt (Application class)
+ NotificationActionReceiver.kt (BroadcastReceiver)

These work together to create notification channels, display actionable notifications, and process user interaction.

1.NotificationHelper.kt — Creating & Displaying Notifications
Location:
app/src/main/java/com/example/medicationadherenceapp/data/utils/NotificationHelper.kt
Purpose:
Centralizes all logic related to notification creation, including building notifications and creating the required NotificationChannel.

🔹 Key Responsibilities
✔ Create Notification Channels
Android 8+ (API 26+) requires every notification to be assigned to a NotificationChannel.
This is aligned with the lecture concept:

Example:
val channel = NotificationChannel(
CHANNEL_ID,
"Medication Reminders",
NotificationManager.IMPORTANCE_HIGH
)

✔ Build Notification UI
NotificationHelper constructs the full notification, including:
+ Title and description
+ Small icon
+ Content PendingIntent (opens the app when tapped)
+ Action PendingIntent (e.g., “Mark as Taken”)

Lecture concept connection:
✔ Provide Action Buttons

The “Mark as Taken” action attaches a PendingIntent that fires a BroadcastReceiver.
This allows:
+ Dismissing the notification
+ Logging the medication dose
+ Running background logic



2.MyApplication.kt — Initializing Notification Channels
Location:
app/src/main/java/com/example/medicationadherenceapp/MyApplication.kt
Purpose:
Ensures all NotificationChannels are created when the app starts, before any notifications are sent.

Channels are created inside onCreate():
override fun onCreate() {
super.onCreate()
NotificationHelper.createChannels(this)
}

You must also register this class in AndroidManifest.xml:
<application
android:name=".MyApplication">


3.NotificationActionReceiver.kt — Handling Notification Actions
Location:
app/src/main/java/com/example/medicationadherenceapp/NotificationActionReceiver.kt
Purpose:
Handles interaction events from action buttons inside the notification, such as “Mark as Taken.”

When the user taps the notification action button, the system triggers the PendingIntent that targets this receiver.
Examples of what this receiver can do:
+ Mark medication as taken
+ Update logs or database
+ Cancel the notification

✔ Works in All App States
Because BroadcastReceivers run independently of activities, this action works even if:
+ The app is in the background
+ The app is in the foreground
+ The app has been killed

📡 How the Components Work Together
flowchart LR
A[MyApplication] -->|Creates NotificationChannel| B[NotificationHelper]
B -->|Builds Notification| C[System Notification Tray]
C -->|User taps Action Button| D[NotificationActionReceiver]
D -->|Handle Action| E[Update Logs / Mark Dose Taken]

✔ Summary of Implemented Features
Feature	Description
Notification Channels	Created on app startup in MyApplication. Required for Android 8+.
High-Priority Notifications	Used for medication reminders to improve visibility.
Actionable Notifications	Include “Mark as Taken” button using a BroadcastReceiver.
PendingIntent Navigation	Tapping the notification opens the main screen.
Background-Friendly	BroadcastReceiver handles actions even when the app is killed.
Clean Architecture	Notification logic isolated inside NotificationHelper.kt.