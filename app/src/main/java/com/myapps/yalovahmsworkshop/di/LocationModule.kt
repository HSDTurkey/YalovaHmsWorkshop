package com.myapps.yalovahmsworkshop.di

import android.content.Context
import com.myapps.yalovahmsworkshop.service.LocationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    fun provideLocation(@ApplicationContext context: Context): LocationService {
        return LocationService(context)
    }

}