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

data class FamilyWithPatients(
    @Embedded val familyMember: User,
    @Relation(
        parentColumn = "familyMemberId",
        entityColumn = "userId",
        associateBy = Junction(FamilyPatientCrossRef::class)
    )
    val patients: List<User>
)

data class PatientWithFamily(
    @Embedded val patient: User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "familyMemberId",
        associateBy = Junction(FamilyPatientCrossRef::class)
    )
    val familyMembers: List<FamilyMember>
)
