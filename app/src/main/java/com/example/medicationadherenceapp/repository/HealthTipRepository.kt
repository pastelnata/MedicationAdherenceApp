package com.example.medicationadherenceapp.repository

import com.example.medicationadherenceapp.data.local.dao.HealthTipDao
import com.example.medicationadherenceapp.data.local.entities.HealthTip
import com.example.medicationadherenceapp.data.remote.NetworkResult
import com.example.medicationadherenceapp.data.remote.RemoteDataSource
import com.example.medicationadherenceapp.data.remote.dto.toEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository for health tips with offline-first and network refresh support.
 *
 * Local-first strategy:
 * - UI always reads from local DB via Flow
 * - Refresh methods fetch from remote and update local cache
 */
@Singleton
class HealthTipRepository @Inject constructor(
    private val dao: HealthTipDao,
    private val remoteDataSource: RemoteDataSource
) {
    // ==================== Local-first operations ====================

    suspend fun addTip(tip: HealthTip) = dao.insertHealthTip(tip)

    fun getTips(): Flow<List<HealthTip>> = dao.getHealthTips()

    suspend fun getTip(id: UUID) = dao.getHealthTip(id)

    suspend fun updateTip(tip: HealthTip) = dao.updateHealthTip(tip)

    suspend fun deleteTip(id: UUID) = dao.deleteHealthTip(id)

    // ==================== Network refresh operations ====================

    /**
     * Refresh health tips from remote server.
     * Fetches latest tips and updates local cache.
     */
    suspend fun refreshHealthTips(limit: Int? = null): NetworkResult<Unit> {
        return when (val result = remoteDataSource.getHealthTips(limit)) {
            is NetworkResult.Success -> {
                // Update local cache with remote data
                result.data.tips.forEach { dto ->
                    dao.insertHealthTip(dto.toEntity())
                }
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    /**
     * Refresh a specific health tip from remote server.
     */
    suspend fun refreshHealthTip(tipId: UUID): NetworkResult<Unit> {
        return when (val result = remoteDataSource.getHealthTip(tipId.toString())) {
            is NetworkResult.Success -> {
                dao.insertHealthTip(result.data.toEntity())
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }
}

