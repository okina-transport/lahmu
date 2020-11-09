package org.entur.lahmu.domain.gbfs.v2_1

import kotlinx.serialization.*

@Serializable
data class GBFS(
    @SerialName("last_updated") override val lastUpdated: Long,
    override val ttl: Int,
    override val version: String,
    override val data: Map<String, Language>
) : GBFSBase() {
    init {
        validate()
    }
}

@Serializable
data class Language(
    val feeds: List<Feed>
)

@Serializable
data class Feed(
    val name: GBFSFeedName,
    val url: String
)