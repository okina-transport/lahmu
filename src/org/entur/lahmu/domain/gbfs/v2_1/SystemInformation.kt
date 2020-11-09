package org.entur.lahmu.domain.gbfs.v2_1

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SystemInformation(
    @Required @SerialName("last_updated") override val lastUpdated: Long,
    @Required override val ttl: Int,
    @Required override val version: String,
    @Required override val data: SystemInformationData
) : GBFSBase()

@Serializable
data class SystemInformationData(
    @Required @SerialName("system_id") val systemId: String,
    @Required val language: String,
    @Required val name: String,
    @SerialName("short_name") val shortName: String? = null,
    val operator: String? = null,
    val url: String? = null,
    @SerialName("purchase_url") val purchaseUrl: String? = null,
    @SerialName("start_date") val startDate: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    val email: String? = null,
    @SerialName("feed_contact_email") val feedContactEmail: String? = null,
    @Required val timezone: String,
    @SerialName("license_url") val licenseUrl: String? = null,
    @SerialName("rental_apps") val rentalApps: RentalApps? = null
)

@Serializable
data class RentalApps(
    val android: RentalApp? = null,
    val ios: RentalApp? = null
)

@Serializable
class RentalApp(
    @Required @SerialName("store_uri") val storeURI: String,
    @Required @SerialName("discovery_uri") val discoveryURI: String
)
