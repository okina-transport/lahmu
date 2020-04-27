package org.entur

val osloBysykkelIndexUrl = "https://gbfs.urbansharing.com/oslobysykkel.no/gbfs.json"

data class BikeResponse(val last_updated: Long, val ttl: Long, val data: BikeResponseData)
data class BikeResponseData(val nb: BikeResponseLanguage)
data class BikeResponseLanguage(val feeds: List<BikeResponseFeed>)
data class BikeResponseFeed(val name: String, val url: String)