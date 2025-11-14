package com.example.medicationadherenceapp.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.medicationadherenceapp.UserType
import java.util.UUID

// User represents app users: patients and family members are modeled with
// a User entry (patients) and a FamilyMember entity linked by a cross-ref.
// Storing userType lets the app enforce role-based UI/behavior.
@Entity
data class User(
    @PrimaryKey val userId: UUID = UUID.randomUUID(),
    val name: String,
    val password: String,
    val userType: UserType
)

// FamilyMember is a separate entity so family members can be linked to
// multiple patients via the cross-reference table below.
@Entity
data class FamilyMember(
    @PrimaryKey val familyMemberId: UUID = UUID.randomUUID(),
    val name: String
)

// Cross-reference for a many-to-many relationship between FamilyMember and
// User (patient). This allows queries in both directions using Room's @Relation.
@Entity(primaryKeys = ["familyMemberId", "patientId"])
data class FamilyPatientCrossRef(
    val familyMemberId: UUID,
    val patientId: UUID
)

// Helper POJOs for Room relations. FamilyWithPatients and PatientWithFamily
// are projection classes that Room populates when a @Transaction query
// requests joined data; they are not stored tables but in-memory views.
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
