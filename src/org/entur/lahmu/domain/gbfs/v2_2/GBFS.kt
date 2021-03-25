package org.entur.lahmu.domain.gbfs.v2_2

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GBFS(
    @Required @SerialName("last_updated") override val lastUpdated: Long,
    @Required override val ttl: Int,
    @Required override val version: String,
    @Required override val data: Map<String, Data>
) : GBFSBase() {

    @Serializable
    data class Data(
        @Required val feeds: List<Feed>
    )

    @Serializable
    data class Feed(
        @Required val name: GBFSFeedName,
        @Required val url: String
    )
}
