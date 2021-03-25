package org.entur.lahmu.domain.gbfs.v2_1

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GBFSVersions(
    @Required @SerialName("last_updated") override val lastUpdated: Long,
    @Required override val ttl: Int,
    @Required override val version: String,
    @Required override val data: Data
) : GBFSBase() {

    @Serializable
    data class Data(
        @Required val versions: List<Version>
    )

    @Serializable
    data class Version(
        @Required val version: String,
        @Required val url: String
    )
}
