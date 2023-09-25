package ru.qwelice.smsmanager.locating.geocoding.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET("/1.x/")
    fun getGeocode(
        @Query("apikey") apiKey: String,
        @Query("format") format: String = "json",
        @Query("geocode") geocode: String
    ) : Call<GeocodeResponse>
}