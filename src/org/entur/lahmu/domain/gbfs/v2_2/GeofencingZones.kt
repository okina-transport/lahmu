package org.entur.lahmu.domain.gbfs.v2_1

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeofencingZones(
    @Required @SerialName("last_updated") override val lastUpdated: Long,
    @Required override val ttl: Int,
    @Required override val version: String,
    @Required override val data: Data
) : GBFSBase() {

    @Serializable
    data class Data(
        @Required @SerialName("geofencing_zones") val geofencingZones: FeatureCollection
    )

    @Serializable
    data class FeatureCollection(
        @Required val type: String = "FeatureCollection",
        @Required val features: Array<Feature>
    )

    @Serializable
    data class Feature(
        val geometry: MultiPolygon,
        val properties: Properties
    )

    @Serializable
    data class Properties(
        val name: String? = null,
        val start: Long? = null,
        val end: Long? = null,
        val rules: List<Rule>? = null
    )

    @Serializable
    data class Rule(
        @SerialName("vehicle_type_ids") val vehicleTypeIds: List<String>? = null,
        @Required @SerialName("ride_allowed") val rideAllowed: Boolean,
        @Required @SerialName("ride_through_allowed") val rideThroughAllowed: Boolean,
        @SerialName("maximum_speed_kph") val maximumSpeedKph: Int? = null
    )
}
