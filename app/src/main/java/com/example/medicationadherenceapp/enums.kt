package com.example.medicationadherenceapp


enum class MedStatus(val statusLabel: String) {
    OVERDUE("Overdue"),
    DUE("Due Soon"),
    TAKEN("Taken"),
}

enum class DrawableIcons(val id: Int) {
    ALARM(R.drawable.alarm_24dp_000000_fill0_wght400_grad0_opsz24),
    LIGHT_BULB(R.drawable.emoji_objects_24dp_000000_fill0_wght400_grad0_opsz24),
}