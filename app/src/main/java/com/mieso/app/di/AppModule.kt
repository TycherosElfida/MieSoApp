package com.mieso.app.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // This makes the dependencies live as long as the app does.
object AppModule {

    /**
     * This function provides a single, app-wide instance of FirebaseFirestore.
     * @Provides tells Hilt that this function is a provider for a dependency.
     * @Singleton ensures that Hilt creates only one instance of Firestore and reuses
     * it everywhere it's needed, which is crucial for performance and correctness.
     *
     * @return A configured instance of FirebaseFirestore.
     */
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    // Later, if we need other global providers (like for Retrofit, SharedPreferences, etc.),
    // we would add them here.
}
