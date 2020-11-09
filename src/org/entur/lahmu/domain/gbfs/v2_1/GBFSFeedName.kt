package org.entur.lahmu.domain.gbfs.v2_1;

import kotlinx.serialization.SerialName;
import kotlinx.serialization.Serializable;

@Serializable
enum class GBFSFeedName {
    @SerialName("gbfs") GBFS,
    @SerialName("gbfs_versions") GBFS_VERSIONS,
    @SerialName("system_information") SYSTEM_INFORMATION,
    @SerialName("vehicle_types") VEHICLE_TYPES,
    @SerialName("station_information") STATION_INFORMATION,
    @SerialName("station_status") STATION_STATUS,
    @SerialName("free_bike_status") FREE_BIKE_STATUS,
    @SerialName("system_hours") SYSTEM_HOURS,
    @SerialName("system_calendar") SYSTEM_CALENDAR,
    @SerialName("system_regions") SYSTEM_REGIONS,
    @SerialName("system_pricing_plans") SYSTEM_PRICING_PLANS,
    @SerialName("system_alerts") SYSTEM_ALERTS,
    @SerialName("geofencing_zones") GEOFENCING_ZONES
}