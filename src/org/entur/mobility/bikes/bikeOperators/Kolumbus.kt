package org.entur.mobility.bikes.bikeOperators

import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset
import org.entur.mobility.bikes.GBFSResponse
import org.entur.mobility.bikes.GbfsStandardEnum
import org.entur.mobility.bikes.StationInformation
import org.entur.mobility.bikes.StationStatus
import org.entur.mobility.bikes.StationStatuses
import org.entur.mobility.bikes.StationsInformation
import org.entur.mobility.bikes.SystemInformation
import org.entur.mobility.bikes.TTL

data class KolumbusResponse(val data: List<KolumbusStation>)
data class KolumbusStation(
    val id: String,
    val external_id: String,
    val name: String,
    val description: String?,
    val capacity: Int,
    val available_slots: Int,
    val available_vehicles: Int,
    val reserved_vehicles: Int,
    val reserved_slots: Int,
    val type: String,
    val latitude: BigDecimal,
    val longitude: BigDecimal
)

fun KolumbusResponse.toSystemInformation(): GBFSResponse.SystemInformationResponse =
    GBFSResponse.SystemInformationResponse(
        last_updated = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
        ttl = TTL,
        data = SystemInformation(
            system_id = "kolumbusbysykkel",
            language = "nb",
            name = "Kolumbus bysykkel",
            timezone = "Europe/Oslo",
            operator = null,
            phone_number = null,
            email = null
        )
    )

fun KolumbusResponse.toStationStatus(): GBFSResponse.StationStatusesResponse =
    GBFSResponse.StationStatusesResponse(
        last_updated = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
        ttl = TTL,
        data = StationStatuses(stations = data.map {
            StationStatus(
                station_id = it.external_id,
                is_installed = 1,
                is_renting = 1,
                is_returning = 1,
                last_reported = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toBigDecimal(),
                num_bikes_available = it.available_vehicles,
                num_docks_available = it.available_slots
            )
        })
    )

fun KolumbusResponse.toStationInformation(): GBFSResponse.StationsInformationResponse =
    GBFSResponse.StationsInformationResponse(
        last_updated = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
        ttl = TTL,
        data = StationsInformation(stations = data.map {
            StationInformation(
                station_id = it.external_id,
                name = it.name,
                address = null,
                lat = it.latitude,
                lon = it.longitude,
                capacity = it.capacity
            )
        })
    )

val kolumbusBysykkelURL = mapOf(
    GbfsStandardEnum.gbfs to "",
    GbfsStandardEnum.system_information to "https://sanntidapi-web-prod.azurewebsites.net/api/parkings?type=CityBike",
    GbfsStandardEnum.station_information to "https://sanntidapi-web-prod.azurewebsites.net/api/parkings?type=CityBike",
    GbfsStandardEnum.station_status to "https://sanntidapi-web-prod.azurewebsites.net/api/parkings?type=CityBike"
)
