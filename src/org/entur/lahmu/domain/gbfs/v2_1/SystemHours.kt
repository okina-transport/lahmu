package org.entur.lahmu.domain.gbfs.v2_1

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SystemHours(
    @Required @SerialName("last_updated") override val lastUpdated: Long,
    @Required override val ttl: Int,
    @Required override val version: String,
    @Required override val data: Data
): GBFSBase() {

    @Serializable
    data class Data(
        @Required @SerialName("rental_hours") val rentalHours: List<RentalHour>
    )

    @Serializable
    data class RentalHour(
        @Required @SerialName("user_types") val userTypes: List<UserType>,
        @Required val days: List<WeekDay>,
        @Required @SerialName("start_time") val startTime: String,
        @Required @SerialName("end_time") val endTime: String
    )

    @Serializable
    enum class UserType {
        @SerialName("member") MEMBER,
        @SerialName("nonmember") NONMEMBER
    }

    @Serializable
    enum class WeekDay {
        @SerialName("mon") MONDAY,
        @SerialName("tue") TUESDAY,
        @SerialName("wed") WEDNESDAY,
        @SerialName("thu") THURSDAY,
        @SerialName("fri") FRIDAY,
        @SerialName("sat") SATURDAY,
        @SerialName("sun") SUNDAY
    }
}