package org.entur.lahmu.bikeOperators

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset
import org.entur.lahmu.GBFSResponse
import org.entur.lahmu.GbfsStandardEnum
import org.entur.lahmu.StationInformation
import org.entur.lahmu.StationStatus
import org.entur.lahmu.StationStatuses
import org.entur.lahmu.StationsInformation
import org.entur.lahmu.SystemInformation
import org.entur.lahmu.SystemPricePlan
import org.entur.lahmu.TTL
import org.entur.lahmu.epochOf31Dec2020
import org.entur.lahmu.epochOf5thJune2020

data class KolumbusResponse(val data: List<KolumbusStation>)
data class KolumbusStation(
    val id: String,
    @SerializedName("external_id") val externalId: String,
    val name: String,
    val description: String?,
    val capacity: Int,
    @SerializedName("available_slots") val availableSlots: Int,
    @SerializedName("available_vehicles") val availableVehicles: Int,
    @SerializedName("reserved_vehicles") val reservedVehicles: Int,
    @SerializedName("reserved_slots") val reservedSlots: Int,
    val type: String,
    val latitude: BigDecimal,
    val longitude: BigDecimal
)

fun KolumbusResponse.jcDecauxSystemInformation(): GBFSResponse.SystemInformationResponse =
    GBFSResponse.SystemInformationResponse(
        lastUpdated = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
        ttl = TTL,
        data = SystemInformation(
            systemId = "kolumbusbysykkel",
            language = "nb",
            name = "Kolumbus bysykkel",
            timezone = "Europe/Oslo",
            operator = null,
            phoneNumber = null,
            email = null
        )
    )

fun KolumbusResponse.toStationStatus(): GBFSResponse.StationStatusesResponse =
    GBFSResponse.StationStatusesResponse(
        lastUpdated = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
        ttl = TTL,
        data = StationStatuses(stations = data.map {
            StationStatus(
                stationId = mapIdToNeTEx(it.externalId, Operator.KOLUMBUSBYSYKKEL),
                isInstalled = 1,
                isRenting = 1,
                isReturning = 1,
                lastReported = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toBigDecimal(),
                numBikesAvailable = it.availableVehicles,
                numDocksAvailable = it.availableSlots
            )
        })
    )

fun KolumbusResponse.toStationInformation(): GBFSResponse.StationsInformationResponse =
    GBFSResponse.StationsInformationResponse(
        lastUpdated = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
        ttl = TTL,
        data = StationsInformation(stations = data.map {
            StationInformation(
                stationId = mapIdToNeTEx(it.externalId, Operator.KOLUMBUSBYSYKKEL),
                name = it.name,
                address = null,
                lat = it.latitude,
                lon = it.longitude,
                capacity = it.capacity
            )
        })
    )

fun kolumbusSystemPricingPlans() = GBFSResponse.SystemPricingPlans(
    lastUpdated = epochOf5thJune2020,
    ttl = getSecondsFrom(epochOf5thJune2020, epochOf31Dec2020),
    plans = listOf(
        SystemPricePlan(
            planId = "636B0671-ED87-42FB-8FAC-6AE8F3A25826",
            url = "https://www.kolumbus.no/Billetter/-priser-og-produkter/bysykkelbillett/",
            name = PricePlan.DAY_PASS_30.toString(),
            currency = "NOK",
            price = 125.0,
            isTaxable = 0,
            description = "Ved sammenhengende bruk i over en time, vil det forekomme et ekstra gebyr på 1.0 NOK per minutt."
        ),
        SystemPricePlan(
            planId = "2AFBF7AD-4EE6-483F-A32A-3A8C94840996",
            url = "https://www.kolumbus.no/verdt-a-vite/sykkel-oversikt/bysykkelen/",
            name = PricePlan.ADD_ON_PASS.toString(),
            currency = "NOK",
            price = 0.0,
            isTaxable = 0,
            description = "Har du en hvilken som helst gyldig billett i appen Kolumbus Billett, eller du jobber i en HjemJobbHjem-bedrift, kan du bruke sykkelen i en time uten å betale noe ekstra."
        )
    )
)

val kolumbusStatusApiUrl = "https://sanntidapi-web-prod.azurewebsites.net/api/parkings?type=CityBike"
val kolumbusBysykkelURL = mapOf(
    GbfsStandardEnum.gbfs to "",
    GbfsStandardEnum.system_information to kolumbusStatusApiUrl,
    GbfsStandardEnum.station_information to kolumbusStatusApiUrl,
    GbfsStandardEnum.station_status to kolumbusStatusApiUrl,
    GbfsStandardEnum.system_pricing_plans to ""
)
