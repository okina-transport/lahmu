package org.entur.lahmu.domain.gbfs.v2_2

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
abstract class GBFSBase {
    @Required @SerialName("last_updated") abstract val lastUpdated: Long
    @Required abstract val ttl: Int
    @Required abstract val version: String
    @Required abstract val data: Any
}
