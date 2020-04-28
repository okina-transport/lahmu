package org.entur

val osloBysykkelIndexUrl = "https://gbfs.urbansharing.com/oslobysykkel.no/gbfs.json"

data class BikeResponse(val last_updated: Long, val ttl: Long, val data: BikeResponseData)
data class BikeResponseData(val nb: BikeResponseLanguage)
data class BikeResponseLanguage(val feeds: List<BikeResponseFeed>)
data class BikeResponseFeed(val name: String, val url: String)

data class SystemInformationResponse(val last_updated: Long, val ttl: Long, val data: SystemInformation)
data class SystemInformation(val system_id: String, val language: String, val name: String, val operator: String, val timezone: String, val phone_number: String, val email: String)