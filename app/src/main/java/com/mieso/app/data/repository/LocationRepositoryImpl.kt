package com.mieso.app.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import com.mieso.app.data.model.UserAddress
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationRepository {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun getCurrentLocation(): Flow<Location?> = callbackFlow {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
            .setWaitForAccurateLocation(true)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                trySend(locationResult.lastLocation).isSuccess
                // Stop location updates after getting one, to save battery
                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        awaitClose { fusedLocationClient.removeLocationUpdates(locationCallback) }
    }

    override suspend fun reverseGeocode(location: Location): UserAddress? {
        // Geocoder can be unreliable on some devices/networks
        if (!Geocoder.isPresent()) return null

        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

            if (addresses.isNullOrEmpty()) {
                null
            } else {
                val address = addresses[0]
                UserAddress(
                    label = "Lokasi Saat Ini",
                    addressLine1 = address.getAddressLine(0) ?: "",
                    city = address.locality ?: "",
                    postalCode = address.postalCode ?: ""
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}