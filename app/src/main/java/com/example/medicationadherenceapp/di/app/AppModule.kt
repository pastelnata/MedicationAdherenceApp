@file:Suppress("unused")
package com.example.medicationadherenceapp.di.app

import android.content.Context
import androidx.room.Room
import com.example.medicationadherenceapp.data.local.AppDatabase
import com.example.medicationadherenceapp.data.local.dao.FamilyDao
import com.example.medicationadherenceapp.data.local.dao.MedicationDao
import com.example.medicationadherenceapp.data.local.dao.MedicationIntakeDao
import com.example.medicationadherenceapp.data.local.dao.UserDao
import com.example.medicationadherenceapp.data.local.dao.MessageDao
import com.example.medicationadherenceapp.data.local.dao.HealthTipDao
import com.example.medicationadherenceapp.data.local.dao.EmergencyContactDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "medication_db").build()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideMedicationDao(db: AppDatabase): MedicationDao = db.medicationDao()

    @Provides
    fun provideMedicationIntakeDao(db: AppDatabase): MedicationIntakeDao = db.medicationIntakeDao()

    @Provides
    fun provideMessageDao(db: AppDatabase): MessageDao = db.messageDao()

    @Provides
    fun provideHealthTipDao(db: AppDatabase): HealthTipDao = db.healthTipDao()

    @Provides
    fun provideEmergencyContactDao(db: AppDatabase): EmergencyContactDao = db.emergencyContactDao()

    @Provides
    fun provideFamilyDao(db: AppDatabase): FamilyDao = db.familyDao()

    // Repositories are provided via constructor injection (@Inject on constructors).
    // Removed redundant @Provides for repositories so Hilt will construct them automatically.

    // we are not implementing this in our app
    // however, this is just to demonstrate how an API would be provided
    /*
    @Provides
    fun provideApiService(): ApiService = Retrofit.Builder()
        .baseUrl("https://your-api-base-url/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
     */
}
