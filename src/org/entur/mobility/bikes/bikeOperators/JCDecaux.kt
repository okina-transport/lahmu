package org.entur.mobility.bikes.bikeOperators

import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset
import org.entur.mobility.bikes.GBFSResponse
import org.entur.mobility.bikes.GbfsStandardEnum
import org.entur.mobility.bikes.LILLESTROM_API_KEY
import org.entur.mobility.bikes.StationInformation
import org.entur.mobility.bikes.StationStatus
import org.entur.mobility.bikes.StationStatuses
import org.entur.mobility.bikes.StationsInformation
import org.entur.mobility.bikes.SystemInformation
import org.entur.mobility.bikes.TTL

val lillestromBysykkelURL = mapOf(
    GbfsStandardEnum.gbfs to "",
    GbfsStandardEnum.system_information to "https://api.jcdecaux.com/vls/v3/stations?contract=lillestrom&apiKey=$LILLESTROM_API_KEY",
    GbfsStandardEnum.station_information to "https://api.jcdecaux.com/vls/v3/stations?contract=lillestrom&apiKey=$LILLESTROM_API_KEY",
    GbfsStandardEnum.station_status to "https://api.jcdecaux.com/vls/v3/stations?contract=lillestrom&apiKey=$LILLESTROM_API_KEY"
)

data class JCDecauxResponse(val data: List<JCDecauxStation>)

data class JCDecauxStation(
    val number: Int,
    val contractName: String,
    val name: String,
    val address: String,
    val position: JCDecauxPosition,
    val banking: Boolean,
    val bonus: Boolean,
    val status: JCDecauxStatus,
    val lastUpdate: String,
    val connected: Boolean,
    val overflow: Boolean,
    val shape: Boolean,
    val totalStands: JCDecauxStands,
    val mainStands: JCDecauxStands,
    val overflowStands: JCDecauxStands?

)

data class JCDecauxPosition(
    val latitude: BigDecimal,
    val longitude: BigDecimal
)

enum class JCDecauxStatus {
    CLOSED, OPEN
}

data class JCDecauxStands(
    val availabilities: JCDecauxAvailabilities,
    val capacity: Int
)

data class JCDecauxAvailabilities(
    val bikes: Int,
    val stands: Int,
    val mechanicalBikes: Int,
    val electricalBikes: Int,
    val electricalInternalBatteryBikes: Int,
    val electricalRemovableBatteryBikes: Int
)

fun JCDecauxResponse.toStationInformation(): GBFSResponse.StationsInformationResponse =
    GBFSResponse.StationsInformationResponse(
        last_updated = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
        ttl = TTL,
        data = StationsInformation(
            stations = data.map {
                StationInformation(
                    station_id = mapIdToNeTEx(it.number.toString(), Operator.LILLESTROMBYSYKKEL),
                    name = it.name,
                    address = it.address,
                    lat = it.position.latitude,
                    lon = it.position.longitude,
                    capacity = it.number
                )
            }
        )
    )

fun JCDecauxResponse.toSystemInformation(): GBFSResponse.SystemInformationResponse =
    GBFSResponse.SystemInformationResponse(
        last_updated = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
        ttl = TTL,
        data = SystemInformation(
            system_id = "lillestrom",
            language = "nb",
            name = "Lillestrøm bysykkel",
            timezone = "Europe/Oslo",
            operator = null,
            phone_number = null,
            email = null
        )
    )

fun JCDecauxResponse.toStationStatus(): GBFSResponse.StationStatusesResponse =
    GBFSResponse.StationStatusesResponse(
        last_updated = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
        ttl = TTL,
        data = StationStatuses(stations = data.map {
            StationStatus(
                station_id = mapIdToNeTEx(it.number.toString(), Operator.LILLESTROMBYSYKKEL),
                is_installed = if (it.connected) 1 else 0,
                is_renting = if (it.status == JCDecauxStatus.OPEN) 1 else 0,
                is_returning = if (it.status == JCDecauxStatus.OPEN) 1 else 0,
                last_reported = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toBigDecimal(),
                num_bikes_available = it.totalStands.availabilities.bikes,
                num_docks_available = it.totalStands.availabilities.stands
            )
        })
    )
