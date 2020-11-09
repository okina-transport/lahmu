package org.entur.lahmu.domain.gbfs.v2_1

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
abstract class GBFSBase {
    @SerialName("last_updated") abstract val lastUpdated: Long
    abstract val ttl: Int
    abstract val version: String
    abstract val data: Any

    fun validate() {
        require(ttl > -1)
    }
}