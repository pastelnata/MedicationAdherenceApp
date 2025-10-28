package com.example.medicationadherenceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.medicationadherenceapp.data.local.entities.HealthTip
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface HealthTipDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthTip(tip: HealthTip)

    @Query("SELECT * FROM HealthTip")
    fun getHealthTips(): Flow<List<HealthTip>>

    @Query("SELECT * FROM HealthTip WHERE tipId = :id")
    suspend fun getHealthTip(id: UUID): HealthTip?

    @Update
    suspend fun updateHealthTip(tip: HealthTip)

    @Query("DELETE FROM HealthTip WHERE tipId = :id")
    suspend fun deleteHealthTip(id: UUID)
}
