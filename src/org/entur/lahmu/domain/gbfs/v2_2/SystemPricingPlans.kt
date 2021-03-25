package org.entur.lahmu.domain.gbfs.v2_1

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SystemPricingPlans(
    @Required @SerialName("last_updated") override val lastUpdated: Long,
    @Required override val ttl: Int,
    @Required override val version: String,
    @Required override val data: Data
) : GBFSBase() {

    @Serializable
    data class Data(
        @Required val plans: List<Plan>
    )

    @Serializable
    data class Plan(
        @Required @SerialName("plan_id") val planId: String,
        val url: String? = null,
        @Required val name: String,
        @Required val currency: String,
        @Required val price: Float,
        @Required @SerialName("is_taxable") val isTaxable: Boolean,
        @Required val description: String,
        @SerialName("per_km_pricing") val perKmPricing: List<PricingSegment>? = null,
        @SerialName("per_min_pricing") val perMinPricing: List<PricingSegment>? = null,
        @SerialName("surge_pricing") val surgePricing: Boolean? = null
    )

    @Serializable
    data class PricingSegment(
        @Required val start: Int,
        @Required val rate: Float,
        @Required val interval: Int,
        val end: Int? = null
    )
}
