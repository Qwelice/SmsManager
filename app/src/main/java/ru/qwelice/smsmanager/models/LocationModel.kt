package ru.qwelice.smsmanager.models

import android.content.Context
import android.location.Location
import ru.qwelice.smsmanager.locating.LocationHelper
import ru.qwelice.smsmanager.utils.PermissionsManager

class LocationModel(private val context: Context, onLocationChanged: (Location) -> Unit) {
    private val locationHelper = LocationHelper(onLocationChanged)
    val isStarted: Boolean
        get() = locationHelper.isStarted
    fun start(){
        if(PermissionsManager.permissionsAreGranted(context, PermissionsManager.mainPermissions)){
            if(PermissionsManager.permissionIsGranted(context, PermissionsManager.backLocationPermission)){
                locationHelper.start(context)
            }
        }
    }
}