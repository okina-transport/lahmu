package org.entur.lahmu.domain.bikeOperators

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.ZoneOffset
import org.entur.lahmu.domain.GBFSResponse
import org.entur.lahmu.domain.GbfsStandardEnum
import org.entur.lahmu.domain.StationInformation
import org.entur.lahmu.domain.StationStatus
import org.entur.lahmu.domain.StationStatuses
import org.entur.lahmu.domain.StationsInformation
import org.entur.lahmu.domain.SystemInformation
import org.entur.lahmu.domain.SystemPricePlan
import org.entur.lahmu.config.TTL
import org.entur.lahmu.config.epochOf31Dec2020
import org.entur.lahmu.config.epochOf5thJune2020

fun drammenBysykkelURL(accessToken: String) = mapOf(
    GbfsStandardEnum.gbfs to "",
    GbfsStandardEnum.system_information to "",
    GbfsStandardEnum.station_information to "https://drammen.pub.api.smartbike.com/api/en/v3/stations.json?access_token=$accessToken",
    GbfsStandardEnum.station_status to "https://drammen.pub.api.smartbike.com/api/en/v3/stations/status.json?access_token=$accessToken",
    GbfsStandardEnum.system_pricing_plans to ""
)

data class DrammenAccessToken(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_in") val expiresIn: Long,
    @SerializedName("token_type") val tokenType: String,
    val scope: String?
)

data class DrammenStationsStatusResponse(val stationsStatus: List<DrammenStationStatus>)
data class DrammenStationStatus(
    val id: String,
    val status: DrammenStatusEnum,
    val availability: DrammenAvailability
)

enum class DrammenStatusEnum {
    OPN, CLS
}

data class DrammenAvailability(
    val bikes: Int,
    val slots: Int
)

data class DrammenStationsResponse(val stations: List<DrammenStation>)
data class DrammenStation(
    val id: String,
    val name: String,
    val address: String,
    val addressNumber: String?,
    val zipCode: String?,
    val districtCode: String?,
    val districtName: String?,
    val altitude: String?,
    val location: DrammenLocation,
    val stationType: String
)

data class DrammenLocation(
    val lat: String,
    val lon: String
)

fun drammenSystemInformation() = GBFSResponse.SystemInformationResponse(
    lastUpdated = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
    ttl = TTL,
    data = SystemInformation(
        systemId = "drammen",
        language = "nb",
        name = "Drammen Bysykkel",
        timezone = "Europe/Oslo",
        operator = null,
        phoneNumber = null,
        email = null
    )
)

fun drammenSystemPricingPlan() = GBFSResponse.SystemPricingPlans(
    lastUpdated = epochOf5thJune2020,
    ttl = getSecondsFrom(epochOf5thJune2020, epochOf31Dec2020),
    plans = listOf(
        SystemPricePlan(
            planId = "8B00A621-82E8-4AC0-9B89-ABEAF99BD238",
            url = "https://www.drammenbysykler.no/nb/info/abonnementer-og-priser",
            name = PricePlan.SEASON_PASS.toString(),
            currency = "NOK",
            price = 130.0,
            isTaxable = 0,
            description = ""
        )
    )
)

fun DrammenStationsResponse.toStationInformation(statusResponse: GBFSResponse.StationStatusesResponse?) =
    GBFSResponse.StationsInformationResponse(
        lastUpdated = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
        ttl = TTL,
        data = StationsInformation(
            stations.map {
                StationInformation(
                    stationId = mapIdToNeTEx(it.id, Operator.DRAMMENBYSYKKEL),
                    address = it.address,
                    lat = it.location.lat.toBigDecimal(),
                    lon = it.location.lon.toBigDecimal(),
                    name = it.name,
                    capacity = statusResponse?.data?.stations?.find { status ->
                        status.stationId == mapIdToNeTEx(
                            it.id,
                            Operator.DRAMMENBYSYKKEL
                        )
                    }?.let { station -> station.numBikesAvailable + station.numDocksAvailable } ?: 0
                )
            }
        )
    )

fun DrammenStationsStatusResponse.toStationStatuses() = GBFSResponse.StationStatusesResponse(
    lastUpdated = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
    ttl = TTL,
    data = StationStatuses(
        stationsStatus.map {
            StationStatus(
                stationId = mapIdToNeTEx(it.id, Operator.DRAMMENBYSYKKEL),
                isInstalled = 1,
                isRenting = if (it.status == DrammenStatusEnum.OPN) 1 else 0,
                isReturning = if (it.status == DrammenStatusEnum.OPN) 1 else 0,
                lastReported = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toBigDecimal(),
                numBikesAvailable = it.availability.bikes,
                numDocksAvailable = it.availability.slots
            )
        }
    )
)
