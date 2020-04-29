package org.entur

import java.math.BigDecimal

fun getOperatorFromPathParam(param: String? = ""): GbfsStandard =
    when(param) {
        "oslobysykkel" -> OsloBysykkelURL
        "bergenbysykkel" -> BergenBysykkelURL
        "trondheimbysykkel" -> TrondheimBysykkelURL
        else -> throw Error("Not implemented")
    }

interface GbfsStandard {
    val gbfs: String
    val system_information: String
    val station_information: String
    val station_status: String
}

object OsloBysykkelURL : GbfsStandard {
    override val gbfs: String = "https://gbfs.urbansharing.com/oslobysykkel.no/gbfs.json"
    override val system_information: String = "https://gbfs.urbansharing.com/oslobysykkel.no/system_information.json"
    override val station_information: String = "https://gbfs.urbansharing.com/oslobysykkel.no/station_information.json"
    override val station_status: String = "https://gbfs.urbansharing.com/oslobysykkel.no/station_status.json"
}

object BergenBysykkelURL : GbfsStandard{
    override val gbfs: String = "https://gbfs.urbansharing.com/bergenbysykkel.no/gbfs.json"
    override val system_information: String = "https://gbfs.urbansharing.com/bergenbysykkel.no/system_information.json"
    override val station_information: String = "https://gbfs.urbansharing.com/bergenbysykkel.no/station_information.json"
    override val station_status: String = "https://gbfs.urbansharing.com/bergenbysykkel.no/station_status.json"
}

object TrondheimBysykkelURL : GbfsStandard{
    override val gbfs: String = "https://gbfs.urbansharing.com/trondheimbysykkel.no/gbfs.json"
    override val system_information: String = "https://gbfs.urbansharing.com/trondheimbysykkel.no/system_information.json"
    override val station_information: String = "https://gbfs.urbansharing.com/trondheimbysykkel.no/station_information.json"
    override val station_status: String = "https://gbfs.urbansharing.com/trondheimbysykkel.no/station_status.json"
}

interface GBFSResponse<T> {
    val last_updated: Long
    val ttl: Long
    val data: T
}
data class BikeResponse(override val last_updated: Long, override val ttl: Long, override val data: BikeResponseData) : GBFSResponse<BikeResponseData>
data class BikeResponseData(val nb: BikeResponseLanguage)
data class BikeResponseLanguage(val feeds: List<BikeResponseFeed>)
data class BikeResponseFeed(val name: String, val url: String)

data class SystemInformationResponse(override val last_updated: Long, override val ttl: Long, override val data: SystemInformation) : GBFSResponse<SystemInformation>
data class SystemInformation(val system_id: String, val language: String, val name: String, val operator: String, val timezone: String, val phone_number: String, val email: String)

data class StationInformationResponse(override val last_updated: Long, override val ttl: Long, override val data: Stations) : GBFSResponse<Stations>
data class Stations(val stations: List<Station>)
data class Station(val station_id: String, val name: String, val address: String, val lat: BigDecimal, val lon: BigDecimal, val capacity: Int)

data class StationStatusResponse(override val last_updated: Long, override val ttl: Long, override val data: StationStatuses) : GBFSResponse<StationStatuses>
data class StationStatuses(val stations: List<StationStatus>)
data class StationStatus(val station_id: String, val is_installed: Int, val is_renting: Int, val is_returning: Int, val last_reported: BigDecimal, val num_bikes_available: Int, val num_docks_available: Int)
