package ru.qwelice.smsmanager.locating

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LastLocationRequest
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import ru.qwelice.smsmanager.utils.PermissionsManager

class LocationHelper(
    private val locationUpdater: (Location)->Unit
) : LocationListener{
    var flp: FusedLocationProviderClient? = null
    var isStarted = false
    @SuppressLint("MissingPermission")
    fun start(context: Context){
        flp = flp ?: LocationServices.getFusedLocationProviderClient(
            context.applicationContext
        )
        if (
            PermissionsManager.permissionsAreGranted(context, PermissionsManager.mainPermissions)
            && PermissionsManager.permissionIsGranted(context, PermissionsManager.backLocationPermission)
        ){
            try {
                flp?.run{
                    getLastLocation(
                        LastLocationRequest.Builder()
                            .setGranularity(Granularity.GRANULARITY_FINE)
                            .setMaxUpdateAgeMillis(7_200_000L)
                            .build()
                    )
                    requestLocationUpdates(
                        LocationRequest.Builder(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            10_000,
                        ).setMinUpdateIntervalMillis(5_000)
                            .setMinUpdateDistanceMeters(30f)
                            .build(),
                        Dispatchers.Default.asExecutor(),
                        this@LocationHelper
                    )
                }
                isStarted = true
            } catch (e: Throwable){

            }
        }
    }

    override fun onLocationChanged(p0: Location) {
        locationUpdater(p0)
    }
}