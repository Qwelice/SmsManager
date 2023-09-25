package ru.qwelice.smsmanager.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher

object PermissionsManager {

    val mainPermissions = arrayOf(
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val backLocationPermission = Manifest.permission.ACCESS_BACKGROUND_LOCATION

    fun checkAndRequestMissingPermission(
        context: Context,
        permission: String,
        launcher: ActivityResultLauncher<String>) : Boolean{
        val permissionResult = context.checkSelfPermission(permission)
        if(permissionResult != PackageManager.PERMISSION_GRANTED){
            launcher.launch(permission)
            return false
        }
        return true
    }

    fun checkAndRequestMissingPermissions(
        context: Context,
        permissions: Array<String>,
        launcher: ActivityResultLauncher<Array<String>>
    ) : Boolean{
        val notGranted = permissions.fold(false){ all,p -> all || context.checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED }
        if(notGranted){
            launcher.launch(permissions)
            return false
        }
        return true
    }

    fun permissionIsGranted(context: Context, permission: String) : Boolean{
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    fun permissionsAreGranted(context: Context, permissions: Array<String>) : Boolean{
        val notGranted = permissions.fold(false){ all,p -> all || context.checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED }
        return !notGranted
    }

    fun requestPermission(context: Context, permission: String, launcher: ActivityResultLauncher<String>){
        if(!permissionIsGranted(context, permission)){
            launcher.launch(permission)
        }
    }

    fun requestPermissions(context: Context, permissions: Array<String>, launcher: ActivityResultLauncher<Array<String>>){
        val notGranted = permissions.fold(false){ all,p -> all || context.checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED }
        if(notGranted){
            launcher.launch(permissions)
        }
    }
}