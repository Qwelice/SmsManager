package ru.qwelice.smsmanager.locating.geocoding.retrofit

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.internal.wait
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    companion object{
        private var instance: RetrofitClient? = null

        fun getInstance() : RetrofitClient{
            if(instance == null){
                instance = RetrofitClient()
            }
            return instance!!
        }
    }

    private val baseUrl = "https://geocode-maps.yandex.ru"
    private val apiKey = "8d5ad3e1-8988-492e-a85d-71a541d7eab0"

    private val retrofit: Retrofit
    init {

        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okClient = OkHttpClient.Builder()
            .addInterceptor(logInterceptor)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getClient() : Retrofit{
        return retrofit
    }

    fun getApi() : GeocodingApi{
        return retrofit.create(GeocodingApi::class.java)
    }

    suspend fun getAddress(latitude: Double, longitude: Double) =
        withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            val api = getApi()
            val geocode = "$longitude,$latitude"
            var result = ""
            try{
                val call = api.getGeocode(
                    apiKey = apiKey,
                    geocode = geocode
                )
                call.enqueue(object : Callback<GeocodeResponse>{
                    override fun onResponse(
                        call: Call<GeocodeResponse>,
                        response: Response<GeocodeResponse>
                    ) {
                        if(response.isSuccessful){
                            val address = response.body()?.response?.geoObjectCollection?.featureMembers?.firstOrNull()?.geoObject?.metaDataProperty?.geocoderMetaData?.address
                            if(address != null){
                                result = address
                            }
                        }
                    }

                    override fun onFailure(call: Call<GeocodeResponse>, t: Throwable) {
                        if(call.isCanceled){

                        }
                    }

                }).wait()
            }catch (ex: Exception){
                ex.printStackTrace()
                Log.d("geocode", ex.message.toString())
            }
            result
        }
}