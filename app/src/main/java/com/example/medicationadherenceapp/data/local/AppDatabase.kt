package com.example.medicationadherenceapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.medicationadherenceapp.data.local.dao.EmergencyContactDao
import com.example.medicationadherenceapp.data.local.dao.FamilyDao
import com.example.medicationadherenceapp.data.local.dao.HealthTipDao
import com.example.medicationadherenceapp.data.local.dao.MedicationDao
import com.example.medicationadherenceapp.data.local.dao.MedicationIntakeDao
import com.example.medicationadherenceapp.data.local.dao.MessageDao
import com.example.medicationadherenceapp.data.local.dao.UserDao
import com.example.medicationadherenceapp.data.local.entities.EmergencyContact
import com.example.medicationadherenceapp.data.local.entities.FamilyMember
import com.example.medicationadherenceapp.data.local.entities.FamilyPatientCrossRef
import com.example.medicationadherenceapp.data.local.entities.HealthTip
import com.example.medicationadherenceapp.data.local.entities.Medication
import com.example.medicationadherenceapp.data.local.entities.MedicationIntakeRecord
import com.example.medicationadherenceapp.data.local.entities.MedicationSchedule
import com.example.medicationadherenceapp.data.local.entities.Message
import com.example.medicationadherenceapp.data.local.entities.User

@Database(
    entities = [
        Medication::class,
        MedicationSchedule::class,
        MedicationIntakeRecord::class,
        User::class,
        FamilyMember::class,
        FamilyPatientCrossRef::class,
        Message::class,
        HealthTip::class,
        EmergencyContact::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao
    abstract fun medicationIntakeDao(): MedicationIntakeDao
    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao
    abstract fun healthTipDao(): HealthTipDao
    abstract fun familyDao(): FamilyDao
    abstract fun emergencyContactDao(): EmergencyContactDao
}