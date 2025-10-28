package com.example.medicationadherenceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.medicationadherenceapp.data.local.entities.FamilyMember
import com.example.medicationadherenceapp.data.local.entities.FamilyPatientCrossRef
import com.example.medicationadherenceapp.data.local.entities.FamilyWithPatients
import java.util.UUID

@Dao
interface FamilyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyMember(member: FamilyMember)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyPatientCrossRef(ref: FamilyPatientCrossRef)

    @Transaction
    @Query("SELECT * FROM FamilyMember WHERE familyMemberId = :id")
    suspend fun getFamilyWithPatients(id: UUID): FamilyWithPatients

    @Query("SELECT * FROM FamilyMember WHERE familyMemberId = :id")
    suspend fun getFamilyMember(id: UUID): FamilyMember?

    @Update
    suspend fun updateFamilyMember(member: FamilyMember)

    @Query("DELETE FROM FamilyPatientCrossRef WHERE familyMemberId = :memberId AND patientId = :patientId")
    suspend fun deleteFamilyPatientCrossRef(memberId: UUID, patientId: UUID)

    @Query("DELETE FROM FamilyMember WHERE familyMemberId = :id")
    suspend fun deleteFamilyMember(id: UUID)
}
