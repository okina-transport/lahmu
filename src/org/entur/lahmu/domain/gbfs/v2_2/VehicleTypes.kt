package org.entur.lahmu.domain.gbfs.v2_1

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VehicleTypes(
    @Required @SerialName("last_updated") override val lastUpdated: Long,
    @Required override val ttl: Int,
    @Required override val version: String,
    @Required override val data: Data
) : GBFSBase() {

    @Serializable
    data class Data(
        @Required @SerialName("vehicle_types") val vehicleTypes: List<VehicleType>
    )

    @Serializable
    data class VehicleType(
        @Required @SerialName("vehicle_type_id") val vehicleTypeId: String,
        @Required @SerialName("form_factor") val formFactor: FormFactor,
        @Required @SerialName("propulsion_type") val propulsionType: PropulsionType,
        @SerialName("max_range_meters") val maxRangeMeters: Float? = null,
        val name: String? = null
    ) {
        init {
            require(maxRangeMetersIsNotNullWhenPropulsionTypeIsNotHuman())
        }

        private fun maxRangeMetersIsNotNullWhenPropulsionTypeIsNotHuman(): Boolean {
            return propulsionType == PropulsionType.HUMAN || maxRangeMeters != null
        }
    }

    @Serializable
    enum class FormFactor {
        @SerialName("bicycle") BICYCLE,
        @SerialName("car") CAR,
        @SerialName("moped") MOPED,
        @SerialName("scooter") SCOOTER,
        @SerialName("other") OTHER
    }

    @Serializable
    enum class PropulsionType {
        @SerialName("human") HUMAN,
        @SerialName("electric_assist") ELECTRIC_ASSIST,
        @SerialName("electric") ELECTRIC,
        @SerialName("combustion") COMBUSTION
    }
}
