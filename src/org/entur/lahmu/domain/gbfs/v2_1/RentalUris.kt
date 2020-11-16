package org.entur.lahmu.domain.gbfs.v2_1

import kotlinx.serialization.Serializable

@Serializable
data class RentalUris(
    val android: String? = null,
    val ios: String? = null,
    val web: String? = null
)
