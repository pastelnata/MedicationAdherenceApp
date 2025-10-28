package com.example.medicationadherenceapp.data.local

import androidx.room.TypeConverter
import com.example.medicationadherenceapp.MedStatus
import com.example.medicationadherenceapp.UserType
import java.util.UUID

object Converters {

    @TypeConverter
    @JvmStatic
    fun fromUUID(uuid: UUID?): String? = uuid?.toString()

    @TypeConverter
    @JvmStatic
    fun toUUID(uuid: String?): UUID? = uuid?.let { UUID.fromString(it) }

    @TypeConverter
    @JvmStatic
    fun fromMedStatus(status: MedStatus?): String? = status?.name

    @TypeConverter
    @JvmStatic
    fun toMedStatus(value: String?): MedStatus? = value?.let { MedStatus.valueOf(it) }

    @TypeConverter
    @JvmStatic
    fun fromUserType(type: UserType?): String? = type?.name

    @TypeConverter
    @JvmStatic
    fun toUserType(value: String?): UserType? = value?.let { UserType.valueOf(it) }
}
