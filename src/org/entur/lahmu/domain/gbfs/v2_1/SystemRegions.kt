package org.entur.lahmu.domain.gbfs.v2_1

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SystemRegions(
    @Required @SerialName("last_updated") override val lastUpdated: Long,
    @Required override val ttl: Int,
    @Required override val version: String,
    @Required override val data: Data
): GBFSBase() {

    @Serializable
    data class Data(
        @Required val regions: List<Region>
    )

    @Serializable
    data class Region(
        @Required @SerialName("region_id") val regionId: String,
        @Required val name: String
    )
}