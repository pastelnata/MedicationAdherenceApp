package com.example.medicationadherenceapp.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.medicationadherenceapp.UserType
import java.util.UUID

@Entity
data class User(
    @PrimaryKey val userId: UUID = UUID.randomUUID(),
    val name: String,
    val password: String,
    val userType: UserType
)

@Entity
data class FamilyMember(
    @PrimaryKey val familyMemberId: UUID = UUID.randomUUID(),
    val name: String
)

@Entity(primaryKeys = ["familyMemberId", "patientId"])
data class FamilyPatientCrossRef(
    val familyMemberId: UUID,
    val patientId: UUID
)

// Family -> Patients (Users)
data class FamilyWithPatients(
    @Embedded val familyMember: FamilyMember,
    @Relation(
        parentColumn = "familyMemberId",
        entityColumn = "userId",
        associateBy = Junction(
            value = FamilyPatientCrossRef::class,
            parentColumn = "familyMemberId",
            entityColumn = "patientId"
        )
    )
    val patients: List<User>
)

// Patient (User) -> FamilyMembers
data class PatientWithFamily(
    @Embedded val patient: User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "familyMemberId",
        associateBy = Junction(
            value = FamilyPatientCrossRef::class,
            parentColumn = "patientId",
            entityColumn = "familyMemberId"
        )
    )
    val familyMembers: List<FamilyMember>
)
