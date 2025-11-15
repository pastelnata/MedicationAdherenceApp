package com.example.medicationadherenceapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response object for syncing patient data.
 * Contains all updated records since the last sync timestamp.
 */
data class SyncResponse(
    @SerializedName("medications") val medications: List<MedicationDto>? = null,
    @SerializedName("schedules") val schedules: List<MedicationScheduleDto>? = null,
    @SerializedName("intakes") val intakes: List<MedicationIntakeDto>? = null,
    @SerializedName("health_tips") val healthTips: List<HealthTipDto>? = null,
    @SerializedName("family_members") val familyMembers: List<FamilyMemberDto>? = null,
    @SerializedName("last_sync_timestamp") val lastSyncTimestamp: Long
)
