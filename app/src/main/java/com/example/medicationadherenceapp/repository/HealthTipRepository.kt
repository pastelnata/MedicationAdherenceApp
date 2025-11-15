package com.example.medicationadherenceapp.repository

import com.example.medicationadherenceapp.data.local.dao.HealthTipDao
import com.example.medicationadherenceapp.data.local.entities.HealthTip
import com.example.medicationadherenceapp.data.remote.ApiService
import com.example.medicationadherenceapp.data.remote.dto.*
import com.example.medicationadherenceapp.data.remote.safeApiCall
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository for health tips. Implements offline-first pattern with remote sync.
 * Health tips are typically fetched from server and cached locally.
 */
class HealthTipRepository @Inject constructor(
    private val dao: HealthTipDao,
    private val apiService: ApiService
) {
    
    /**
     * Returns Flow of tips from local database for reactive UI.
     */
    fun getTips(): Flow<List<HealthTip>> = dao.getHealthTips()
    
    suspend fun getTip(id: UUID) = dao.getHealthTip(id)
    
    suspend fun addTip(tip: HealthTip): Result<HealthTip> {
        return try {
            val result = safeApiCall {
                apiService.createHealthTip(
                    CreateHealthTipRequest(
                        title = tip.title,
                        description = tip.description
                    )
                )
            }
            
            when (result) {
                is Result.Success -> {
                    val entity = result.data.toEntity()
                    dao.insertHealthTip(entity)
                    Result.Success(entity)
                }
                is Result.Error -> {
                    dao.insertHealthTip(tip)
                    Result.Success(tip)
                }
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            dao.insertHealthTip(tip)
            Result.Success(tip)
        }
    }
    
    suspend fun updateTip(tip: HealthTip): Result<HealthTip> {
        return try {
            val result = safeApiCall {
                apiService.updateHealthTip(tip.tipId.toString(), tip.toDto())
            }
            
            dao.updateHealthTip(tip)
            
            when (result) {
                is Result.Success -> Result.Success(tip)
                is Result.Error -> Result.Success(tip) // Updated locally
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            dao.updateHealthTip(tip)
            Result.Success(tip)
        }
    }
    
    suspend fun deleteTip(id: UUID): Result<Unit> {
        return try {
            val result = safeApiCall {
                apiService.deleteHealthTip(id.toString())
            }
            
            dao.deleteHealthTip(id)
            
            when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> Result.Success(Unit) // Deleted locally
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            dao.deleteHealthTip(id)
            Result.Success(Unit)
        }
    }
    
    /**
     * Fetch latest health tips from server and cache locally.
     * @param limit Number of tips to fetch
     * @param offset Offset for pagination
     */
    suspend fun syncHealthTips(limit: Int? = null, offset: Int? = null): Result<List<HealthTip>> {
        return try {
            val result = safeApiCall {
                apiService.getHealthTips(limit, offset)
            }
            
            when (result) {
                is Result.Success -> {
                    val tips = result.data.map { it.toEntity() }
                    tips.forEach { dao.insertHealthTip(it) }
                    Result.Success(tips)
                }
                is Result.Error -> result
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            Result.Error(e, e.message)
        }
    }
}
