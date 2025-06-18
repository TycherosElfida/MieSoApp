package com.mieso.app.data.repository

import android.location.Location
import com.mieso.app.data.model.UserAddress
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getCurrentLocation(): Flow<Location?>
    suspend fun reverseGeocode(location: Location): UserAddress?
}