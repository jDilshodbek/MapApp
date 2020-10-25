package uz.test.mapapp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class Yandex(
    @SerializedName("response")
    val response: Response
)

data class Response(
    @SerializedName("GeoObjectCollection")
    val geoObjectCollection: GeoObjectCollection
)


data class GeoObjectCollection(
    @SerializedName("featureMember")
    val featureMember: List<FeatureMember>
)


data class FeatureMember(
    @SerializedName("GeoObject")
    val geoObject: GeoObject
)

data class GeoObject(
    @SerializedName("metaDataProperty")
    val metaDataProperty: MetaDataProperty,
    @SerializedName("Point")
    val point: Point
)


data class MetaDataProperty(
    @SerializedName("GeocoderMetaData")
    val geocoderMetaData: GeocoderMetaData
)


data class GeocoderMetaData(
    @SerializedName("Address")
    val address: Address
)

@Entity(tableName = "address")
data class Address(
    @PrimaryKey(autoGenerate = true)
    var address_id: Int = 0,
    @SerializedName("formatted")
    @ColumnInfo(name = "formatted")
    val formatted: String,
    @ColumnInfo(name = "lat")
    val lat: Double,
    @ColumnInfo(name = "lng")
    val lng: Double
)

data class Point(
    @SerializedName("pos")
    val pos: String
)