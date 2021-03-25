package org.entur.lahmu.domain.gbfs.v2_2

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StationInformation(
    @Required @SerialName("last_updated") override val lastUpdated: Long,
    @Required override val ttl: Int,
    @Required override val version: String,
    @Required override val data: Data
) : GBFSBase() {
    @Serializable
    data class Data(
        @Required val stations: List<Station>
    ) {
        @Serializable
        data class Station(
            @Required @SerialName("station_id") val stationId: String,
            @Required val name: String,
            @SerialName("short_name") val shortName: String? = null,
            @Required val lat: Double,
            @Required val lon: Double,
            val address: String? = null,
            @SerialName("cross_street") val crossStreet: String? = null,
            @SerialName("region_id") val regionId: String? = null,
            @SerialName("post_code") val postCode: String? = null,
            @SerialName("rental_methods") val rentalMethods: List<RentalMethod>? = null,
            @SerialName("is_virtual_station") val isVirtualStation: Boolean? = null,
            @SerialName("station_area") val stationArea: MultiPolygon? = null,
            val capacity: Int? = null,
            @SerialName("vehicle_capacity") val vehicleCapacity: Map<String, Int>? = null,
            @SerialName("is_valet_station") val isValetStation: Boolean? = null,
            @SerialName("rental_uris") val rentalUris: RentalUris? = null,
            @SerialName("vehicle_type_capacity") val vehicleTypeCapacity: Map<String, Int>? = null
        )

        @Serializable
        enum class RentalMethod {
            KEY,
            CREDITCARD,
            PAYPASS,
            APPLEPAY,
            ANDROIDPAY,
            TRANSITCARD,
            ACCOUNTNUMBER,
            PHONE
        }
    }
}
