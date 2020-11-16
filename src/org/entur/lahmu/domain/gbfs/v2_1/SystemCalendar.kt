package org.entur.lahmu.domain.gbfs.v2_1

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SystemCalendar(
    @Required @SerialName("last_updated") override val lastUpdated: Long,
    @Required override val ttl: Int,
    @Required override val version: String,
    @Required override val data: Data
): GBFSBase() {

    @Serializable
    data class Data(
        @Required val calendars: List<Calendar>
    )

    @Serializable
    data class Calendar(
        @Required @SerialName("start_month") val startMonth: Int,
        @Required @SerialName("start_day") val startDay: Int,
        @SerialName("start_year") val startYear: Int? = null,
        @Required @SerialName("end_month") val endMonth: Int,
        @Required @SerialName("end_day") val endDay: Int,
        @SerialName("end_year") val endYear: Int? = null,
    )
}