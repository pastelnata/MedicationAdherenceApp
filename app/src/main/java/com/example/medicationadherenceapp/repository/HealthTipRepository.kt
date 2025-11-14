package com.example.medicationadherenceapp.repository

import com.example.medicationadherenceapp.data.local.dao.HealthTipDao
import com.example.medicationadherenceapp.data.local.entities.HealthTip
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository for health tips. Currently local-only; returns Flow of tips so
 * the UI lists update automatically when the DB changes.
 */
class HealthTipRepository @Inject constructor(private val dao: HealthTipDao) {
    suspend fun addTip(tip: HealthTip) = dao.insertHealthTip(tip)
    fun getTips(): Flow<List<HealthTip>> = dao.getHealthTips()

    suspend fun getTip(id: UUID) = dao.getHealthTip(id)
    suspend fun updateTip(tip: HealthTip) = dao.updateHealthTip(tip)
    suspend fun deleteTip(id: UUID) = dao.deleteHealthTip(id)
}
