package org.entur.lahmu.domain.gbfs.v2_1

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SystemAlerts(
    @Required @SerialName("last_updated") override val lastUpdated: Long,
    @Required override val ttl: Int,
    @Required override val version: String,
    @Required override val data: Data
) : GBFSBase() {

    @Serializable
    data class Data(
        @Required val alerts: List<Alert>
    )

    @Serializable
    data class Alert(
        @Required @SerialName("alert_id") val alertId: String,
        @Required val type: AlertType,
        val times: List<AlertTime>? = null,
        @SerialName("station_ids") val stationIds: List<String>? = null,
        @SerialName("region_ids") val regionIds: List<String>? = null,
        val url: String? = null,
        @Required val summary: String,
        val description: String? = null,
        @SerialName("last_updated") val lastUpdated: Long? = null
    )

    @Serializable
    enum class AlertType {
        SYSTEM_CLOSURE,
        STATION_CLOSURE,
        STATION_MOVE,
        OTHER
    }

    @Serializable
    data class AlertTime(
        @Required val start: Long,
        val end: Long? = null
    )
}
