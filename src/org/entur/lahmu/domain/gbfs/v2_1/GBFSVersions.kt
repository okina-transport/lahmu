package org.entur.lahmu.domain.gbfs.v2_1

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GBFSVersions(
    @SerialName("last_updated") override val lastUpdated: Long,
    override val ttl: Int,
    override val version: String,
    override val data: VersionsData
    ): GBFSBase()

@Serializable
data class VersionsData (
    val versions: List<Version>
)

@Serializable
data class Version (
    val version: String,
    val url: String
)