package ru.qwelice.smsmanager.locating.geocoding.retrofit

import com.google.gson.annotations.SerializedName

data class GeocodeResponse(
    @SerializedName("response")
    val response: GeocodeResult
)

data class GeocodeResult(
    @SerializedName("GeoObjectCollection")
    val geoObjectCollection: GeoObjectCollection
)

data class GeoObjectCollection(
    @SerializedName("featureMember")
    val featureMembers: List<FeatureMember>
)

data class FeatureMember(
    @SerializedName("GeoObject")
    val geoObject: GeoObject
)

data class GeoObject(
    @SerializedName("metaDataProperty")
    val metaDataProperty: MetaDataProperty
)

data class MetaDataProperty(
    @SerializedName("GeocoderMetaData")
    val geocoderMetaData: GeocoderMetaData
)

data class GeocoderMetaData(
    @SerializedName("text")
    val address: String
)