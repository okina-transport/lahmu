package org.entur.lahmu.legacy

import com.google.gson.annotations.SerializedName
import io.ktor.http.hostIsIp
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset
import org.entur.lahmu.legacy.bikeOperators.Operator
import org.entur.lahmu.legacy.bikeOperators.Operator.Companion.getFetchUrls
import org.entur.lahmu.legacy.bikeOperators.mapIdToNeTEx

enum class GbfsStandardEnum {
    gbfs,
    system_information,
    station_information,
    station_status,
    system_pricing_plans,
    free_bike_status,
    vehicle_types;

    companion object {
        fun GbfsStandardEnum.getFetchUrl(operator: Operator, accessToken: String = ""): String =
            operator.getFetchUrls(accessToken).getValue(this)
        }
    }

sealed class GBFSResponse(
    @SerializedName("last_updated") val lastUpdated: Long,
    val ttl: Long
) {
    class DiscoveryResponse(lastUpdated: Long, ttl: Long, val data: Discovery) :
        GBFSResponse(lastUpdated, ttl)

    class SystemInformationResponse(lastUpdated: Long, ttl: Long, val data: SystemInformation) :
        GBFSResponse(lastUpdated, ttl)

    class StationsInformationResponse(lastUpdated: Long, ttl: Long, val data: StationsInformation) :
        GBFSResponse(lastUpdated, ttl) {
        fun toNeTEx(operator: Operator): StationsInformationResponse =
            StationsInformationResponse(
                lastUpdated = lastUpdated,
                ttl = ttl,
                data = this.data.toNeTEx(operator)
            )
    }

    class StationStatusesResponse(lastUpdated: Long, ttl: Long, val data: StationStatuses) :
        GBFSResponse(lastUpdated, ttl) {
        fun toNeTEx(operator: Operator) = StationStatusesResponse(
            lastUpdated = lastUpdated,
            ttl = ttl,
            data = data.toNeTEx(operator)
        )
    }

    class SystemPricingPlans(lastUpdated: Long, ttl: Long, val plans: List<SystemPricePlan>) : GBFSResponse(lastUpdated, ttl)
}

data class Discovery(val nb: DiscoveryLanguage)
data class DiscoveryLanguage(val feeds: List<DiscoveryFeed>)
data class DiscoveryFeed(val name: String, val url: String)

data class SystemInformation(
    @SerializedName("system_id") val systemId: String,
    val language: String,
    val name: String,
    val operator: String?,
    val timezone: String,
    @SerializedName("phone_number") val phoneNumber: String?,
    val email: String?
)

data class StationsInformation(val stations: List<StationInformation>)
data class StationInformation(
    @SerializedName("station_id") val stationId: String,
    val name: String,
    val address: String?,
    val lat: BigDecimal,
    val lon: BigDecimal,
    val capacity: Int
)

data class StationStatuses(val stations: List<StationStatus>)
data class StationStatus(
    @SerializedName("station_id") val stationId: String,
    @SerializedName("is_installed") val isInstalled: Int,
    @SerializedName("is_renting") val isRenting: Int,
    @SerializedName("is_returning") val isReturning: Int,
    @SerializedName("last_reported") val lastReported: BigDecimal,
    @SerializedName("num_bikes_available") val numBikesAvailable: Int,
    @SerializedName("num_docks_available") val numDocksAvailable: Int
)

data class SystemPricePlan(
    @SerializedName("plan_id") val planId: String,
    val url: String?,
    val name: String,
    val currency: String,
    val price: Double,
    @SerializedName("is_taxable") val isTaxable: Int,
    val description: String
)

fun StationsInformation.toNeTEx(operator: Operator): StationsInformation =
    StationsInformation(
        stations = stations.map { station ->
            station.toNeTEx(operator)
        }
    )

fun StationInformation.toNeTEx(operator: Operator): StationInformation =
    StationInformation(
        stationId = mapIdToNeTEx(stationId, operator),
        name = name,
        address = address,
        lat = lat,
        lon = lon,
        capacity = capacity
    )

fun StationStatuses.toNeTEx(operator: Operator): StationStatuses =
    StationStatuses(
        stations = stations.map { stationStatus -> stationStatus.toNeTEx(operator) }
    )

fun StationStatus.toNeTEx(operator: Operator): StationStatus =
    StationStatus(
        stationId = mapIdToNeTEx(stationId, operator),
        isInstalled = isInstalled,
        isRenting = isRenting,
        isReturning = isReturning,
        lastReported = lastReported,
        numBikesAvailable = numBikesAvailable,
        numDocksAvailable = numDocksAvailable
    )

fun getDiscovery(gbfsStandard: Map<GbfsStandardEnum, String>): GBFSResponse =
    GBFSResponse.DiscoveryResponse(
        lastUpdated = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
        ttl = 15,
        data = Discovery(
            nb = DiscoveryLanguage(
                feeds = listOf(
                    DiscoveryFeed(
                        name = "system_information",
                        url = gbfsStandard.getValue(GbfsStandardEnum.system_information)
                    ),
                    DiscoveryFeed(
                        name = "station_information",
                        url = gbfsStandard.getValue(GbfsStandardEnum.station_information)
                    ),
                    DiscoveryFeed(
                        name = "station_status",
                        url = gbfsStandard.getValue(GbfsStandardEnum.station_status)
                    ),
                    DiscoveryFeed(
                        name = "system_pricing_plans",
                        url = gbfsStandard.getValue(GbfsStandardEnum.system_pricing_plans)
                    )
                )
            )
        )
    )

fun getGbfsEndpoint(operator: Operator, host: String, port: Int): Map<GbfsStandardEnum, String> {
    val modifiedHost = host.replace("lahmu", "api")
    val urlHost = if (modifiedHost == "localhost" || hostIsIp(host)) "http://$modifiedHost:$port/bikes" else "https://$modifiedHost/bikes"
    return mapOf(
        GbfsStandardEnum.gbfs to "$urlHost/$operator/gbfs.json".toLowerCase(),
        GbfsStandardEnum.system_information to "$urlHost/$operator/system_information.json".toLowerCase(),
        GbfsStandardEnum.station_information to "$urlHost/$operator/station_information.json".toLowerCase(),
        GbfsStandardEnum.station_status to "$urlHost/$operator/station_status.json".toLowerCase(),
        GbfsStandardEnum.system_pricing_plans to "$urlHost/$operator/system_pricing_plans.json".toLowerCase(),
        GbfsStandardEnum.vehicle_types to "$urlHost/$operator/vehicle_types.json".toLowerCase()
    )
}
