package org.entur.lahmu.domain.bikeOperators

import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset
import org.entur.lahmu.config.LILLESTROM_API_KEY
import org.entur.lahmu.config.TTL
import org.entur.lahmu.config.epochOf31Dec2020
import org.entur.lahmu.config.epochOf5thJune2020
import org.entur.lahmu.domain.GBFSResponse
import org.entur.lahmu.domain.GbfsStandardEnum
import org.entur.lahmu.domain.StationInformation
import org.entur.lahmu.domain.StationStatus
import org.entur.lahmu.domain.StationStatuses
import org.entur.lahmu.domain.StationsInformation
import org.entur.lahmu.domain.SystemInformation
import org.entur.lahmu.domain.SystemPricePlan

val lillestromBysykkelURL = mapOf(
    GbfsStandardEnum.gbfs to "",
    GbfsStandardEnum.system_information to "https://api.jcdecaux.com/vls/v3/stations?contract=lillestrom&apiKey=$LILLESTROM_API_KEY",
    GbfsStandardEnum.station_information to "https://api.jcdecaux.com/vls/v3/stations?contract=lillestrom&apiKey=$LILLESTROM_API_KEY",
    GbfsStandardEnum.station_status to "https://api.jcdecaux.com/vls/v3/stations?contract=lillestrom&apiKey=$LILLESTROM_API_KEY",
    GbfsStandardEnum.system_pricing_plans to ""
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
        lastUpdated = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
        ttl = TTL,
        data = StationsInformation(
            stations = data.map {
                StationInformation(
                    stationId = mapIdToNeTEx(it.number.toString(), Operator.LILLESTROMBYSYKKEL),
                    name = it.name,
                    address = it.address,
                    lat = it.position.latitude,
                    lon = it.position.longitude,
                    capacity = it.number
                )
            }
        )
    )

fun jcDecauxSystemInformation(): GBFSResponse.SystemInformationResponse =
    GBFSResponse.SystemInformationResponse(
        lastUpdated = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
        ttl = TTL,
        data = SystemInformation(
            systemId = "lillestrom",
            language = "nb",
            name = "Lillestrøm bysykkel",
            timezone = "Europe/Oslo",
            operator = null,
            phoneNumber = null,
            email = null
        )
    )

fun JCDecauxResponse.toStationStatus(): GBFSResponse.StationStatusesResponse =
    GBFSResponse.StationStatusesResponse(
        lastUpdated = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
        ttl = TTL,
        data = StationStatuses(stations = data.map {
            StationStatus(
                stationId = mapIdToNeTEx(it.number.toString(), Operator.LILLESTROMBYSYKKEL),
                isInstalled = if (it.connected) 1 else 0,
                isRenting = if (it.status == JCDecauxStatus.OPEN) 1 else 0,
                isReturning = if (it.status == JCDecauxStatus.OPEN) 1 else 0,
                lastReported = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toBigDecimal(),
                numBikesAvailable = it.totalStands.availabilities.bikes,
                numDocksAvailable = it.totalStands.availabilities.stands
            )
        })
    )

fun jcDecauxSystemPricingPlans(): GBFSResponse.SystemPricingPlans =
    GBFSResponse.SystemPricingPlans(
        lastUpdated = epochOf5thJune2020,
        ttl = getSecondsFrom(epochOf5thJune2020, epochOf31Dec2020),
        plans = listOf(
            SystemPricePlan(
                planId = "D16E7EC0-47F5-427D-9B71-CD079F989CC6",
                url = "http://www.bysykkel.org/Abonnement/Satser",
                name = PricePlan.SEASON_PASS.toString(),
                currency = "NOK",
                price = 50.0,
                isTaxable = 0,
                description = "Hvis sykkelturen varer i mer enn 60 minutter, påløper et bruksgebyr på 20.0 NOK den " +
                    "første halvtimen, og 40.0 NOK for alle påbegynte halvtimer etter det."
            ),
            SystemPricePlan(
                planId = "867E4558-77E3-4608-8941-0C667E924280",
                url = "http://www.bysykkel.org/Abonnement/Satser",
                name = PricePlan.DAY_PASS_3.toString(),
                currency = "NOK",
                price = 10.0,
                isTaxable = 0,
                description = "Hvis sykkelturen varer i mer enn 60 minutter, påløper et bruksgebyr på 20.0 NOK den " +
                    "første halvtimen, og 40.0 NOK for alle påbegynte halvtimer etter det."
            )
        )
    )
