package org.entur.lahmu.domain.gbfs.v2_2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("MultiPolygon")
data class MultiPolygon(
    val coordinates: Surface
)
