package com.example.carrentapp.di

import android.content.Context
import com.example.carrentapp.data.repository.CarRentalRepository
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /**
     * Provides the CarRentalRepository instance for dependency injection.
     */
    @Provides
    @Singleton
    fun provideCarRentalRepository(): CarRentalRepository = CarRentalRepository()

    /**
     * Provides the PlacesClient instance for Google Places API.
     * @param context Application context to initialize PlacesClient.
     */
    @Provides
    @Singleton
    fun providePlacesClient(@ApplicationContext context: Context): PlacesClient {
        return Places.createClient(context)
    }
}