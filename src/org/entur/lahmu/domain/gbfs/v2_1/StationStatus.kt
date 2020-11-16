package org.entur.lahmu.domain.gbfs.v2_1

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StationStatus(
    @Required @SerialName("last_updated") override val lastUpdated: Long,
    @Required override val ttl: Int,
    @Required override val version: String,
    @Required override val data: Data,
): GBFSBase() {

    @Serializable
    data class Data (
        @Required val stations: List<Station>
    )

    @Serializable
    data class Station (
        @Required @SerialName("station_id") val stationId: String,
        @Required @SerialName("is_installed") val isInstalled: Boolean,
        @Required @SerialName("is_renting") val isRenting: Boolean,
        @Required @SerialName("is_returning") val isReturning: Boolean,
        @Required @SerialName("last_reported") val lastReported: Long,
        @Required @SerialName("num_bikes_available") val numBikesAvailable: Int,
        @SerialName("num_docks_available") val numDocksAvailable: Int? = null,
        @SerialName("vehicle_docks_available") val vehicleDocksAvailable: List<VehicleDockAvailability>? = null,
        @SerialName("vehicle_types_available") val vehicleTypesAvailable: List<VehicleTypeAvailability>? = null,
        @SerialName("num_bikes_disabled") val numBikesDisabled: Int? = null,
        @SerialName("num_docks_disabled") val numDocksDisabled: Int? = null,
    )

    @Serializable
    data class VehicleTypeAvailability (
        @Required @SerialName("vehicle_type_id") val vehicleTypeId: String,
        @Required val count: Int
    )

    @Serializable
    data class VehicleDockAvailability(
        @Required @SerialName("vehicle_type_ids") val vehicleTypeIds: List<String>,
        @Required val count: Int
    )
}






