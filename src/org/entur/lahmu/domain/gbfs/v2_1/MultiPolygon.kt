package org.entur.lahmu.domain.gbfs.v2_1

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("MultiPolygon")
data class MultiPolygon(
    val coordinates: Surface
)
