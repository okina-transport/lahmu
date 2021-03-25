package org.entur.lahmu.domain.gbfs.v2_2

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FreeBikeStatus(
    @Required @SerialName("last_updated") override val lastUpdated: Long,
    @Required override val ttl: Int,
    @Required override val version: String,
    @Required override val data: Data
) : GBFSBase() {

    @Serializable
    data class Data(
        val bikes: List<Bike>
    )

    @Serializable
    data class Bike(
        @SerialName("bike_id") val bikeId: String,
        @SerialName("last_reported") val lastReported: Long? = null,
        val lat: Double? = null,
        val lon: Double? = null,
        @Required @SerialName("is_reserved") val isReserved: Boolean,
        @Required @SerialName("is_disabled") val isDisabled: Boolean,
        @SerialName("vehicle_type_id") val vehicleTypeId: String? = null,
        @SerialName("rental_uris") val rentalUris: RentalUris? = null,
        @SerialName("current_range_meters") val currentRangeMeters: Float? = null,
        @SerialName("station_id") val stationId: String? = null,
        @SerialName("pricing_plan_id") val pricingPlanId: String? = null
    )
}
