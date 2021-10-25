package org.entur.lahmu.web.controllers

import io.ktor.application.ApplicationCall
import io.ktor.features.NotFoundException
import io.ktor.http.ContentType
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.response.respondText
import java.lang.Exception
import java.util.stream.Collectors
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.entur.lahmu.domain.gbfs.v2_2.GBFS
import org.entur.lahmu.domain.gbfs.v2_2.GBFSFeedName
import org.entur.lahmu.domain.gbfs.v2_2.StationInformation
import org.entur.lahmu.domain.gbfs.v2_2.StationStatus
import org.entur.lahmu.domain.gbfs.v2_2.SystemInformation
import org.entur.lahmu.domain.gbfs.v2_2.SystemPricingPlans
import org.entur.lahmu.domain.gbfs.v2_2.VehicleTypes
import org.entur.lahmu.legacy.GBFSResponse
import org.entur.lahmu.legacy.GbfsStandardEnum
import org.entur.lahmu.legacy.bikeOperators.Operator
import org.entur.lahmu.legacy.bikeOperators.Operator.Companion.getCodeSpace
import org.entur.lahmu.legacy.bikeOperators.Operator.Companion.getPropulsionType
import org.entur.lahmu.legacy.getDiscovery
import org.entur.lahmu.legacy.getGbfsEndpoint
import org.entur.lahmu.legacy.service.BikeService
import org.entur.lahmu.legacy.service.Cache
import org.entur.lahmu.logger

interface ProxyController {
    suspend fun getDiscoveryFeed(call: ApplicationCall)
    suspend fun getVehicleTypesFeed(call: ApplicationCall)
    suspend fun getGbfsFeed(call: ApplicationCall)
}

class ProxyControllerImpl(private val bikeService: BikeService, private val cache: Cache) : ProxyController {
    override suspend fun getDiscoveryFeed(call: ApplicationCall) {
        val operator = getOperator(call) ?: throw NotFoundException()
        val gbfsEndpoints = getGbfsEndpoint(operator, call.request.local.scheme, call.request.host(), call.request.port())
        val discovery: GBFSResponse.DiscoveryResponse = getDiscovery(gbfsEndpoints) as GBFSResponse.DiscoveryResponse

        val feeds = discovery.data.nb.feeds.stream().map {
            GBFS.Feed(
                name = GBFSFeedName.valueOf(it.name.toUpperCase()),
                url = it.url.replace("/bikes", "/bikes/v2_2_proxy")
            )
        }.collect(Collectors.toList())

        feeds.add(GBFS.Feed(
            name = GBFSFeedName.VEHICLE_TYPES,
            url = (gbfsEndpoints.get(GbfsStandardEnum.vehicle_types) as String).replace("/bikes", "/bikes/v2_2_proxy")
        ))

        val v2 = GBFS(
            lastUpdated = discovery.lastUpdated,
            ttl = discovery.ttl.toInt(),
            version = "2.2",
            data = mapOf(
                "nb" to GBFS.Data(feeds)
            ))

        call.respondText(ContentType.Application.Json) { Json.encodeToString(v2) }
    }

    override suspend fun getVehicleTypesFeed(call: ApplicationCall) {
        val operator = getOperator(call) ?: throw NotFoundException()
        val gbfsEndpoints = getGbfsEndpoint(operator, call.request.local.scheme, call.request.host(), call.request.port())
        call.respondText(ContentType.Application.Json) {
            Json.encodeToString(mapVehicleTypes(operator, gbfsEndpoints))
        }
    }

    private fun mapVehicleTypes(operator: Operator, gbfsEndpoints: Map<GbfsStandardEnum, String>): VehicleTypes {
        val discovery: GBFSResponse.DiscoveryResponse = getDiscovery(gbfsEndpoints) as GBFSResponse.DiscoveryResponse
        return VehicleTypes(
            lastUpdated = discovery.lastUpdated,
            ttl = discovery.ttl.toInt(),
            version = "2.2",
            data = VehicleTypes.Data(
                vehicleTypes = listOf(
                    VehicleTypes.VehicleType(
                        vehicleTypeId = "${operator.getCodeSpace()}:VehicleType:CityBike",
                        formFactor = VehicleTypes.FormFactor.BICYCLE,
                        propulsionType = operator.getPropulsionType(),
                        maxRangeMeters = if (operator.getPropulsionType() == VehicleTypes.PropulsionType.HUMAN) null else 0F
                    )
                )
            )
        )
    }

    override suspend fun getGbfsFeed(call: ApplicationCall) {
        val operator = getOperator(call) ?: throw NotFoundException()
        val gbfsEnum = getGbfsEnum(call) ?: throw NotFoundException()

        if (!cache.isValidCache(operator, gbfsEnum)) {
            try {
                bikeService.fetchAndStoreInCache(
                    cache = cache,
                    operator = operator,
                    gbfsStandardEnum = gbfsEnum
                )
            } catch (e: Exception) {
                logger.error("Failed to fetch $gbfsEnum from $operator. $e")
            }
        }
        val result = cache.getResponseFromCache(operator, gbfsEnum) ?: throw NotFoundException()

        when (gbfsEnum) {
            GbfsStandardEnum.system_information -> {
                call.respondText(ContentType.Application.Json) {
                    Json.encodeToString(mapSystemInformation(operator, result as GBFSResponse.SystemInformationResponse))
                }
            }
            GbfsStandardEnum.station_information -> {
                call.respondText(ContentType.Application.Json) {
                    Json.encodeToString(mapStationInformation(result as GBFSResponse.StationsInformationResponse))
                }
            }
            GbfsStandardEnum.station_status -> {
                call.respondText(ContentType.Application.Json) {
                    Json.encodeToString(mapStationStatus(operator, result as GBFSResponse.StationStatusesResponse))
                }
            }
            GbfsStandardEnum.system_pricing_plans -> {
                call.respondText(ContentType.Application.Json) {
                    Json.encodeToString(mapSystemPricingPlans(operator, result as GBFSResponse.SystemPricingPlans))
                }
            }
            else -> throw NotFoundException()
        }
    }

    private fun mapSystemInformation(operator: Operator, result: GBFSResponse.SystemInformationResponse): SystemInformation {
        var systemId = result.data.systemId

        if (operator == Operator.TRONDHEIMBYSYKKEL) {
            systemId = "${result.data.systemId}bysykkel"
        }

        return SystemInformation(
            lastUpdated = result.lastUpdated,
            ttl = result.ttl.toInt(),
            version = "2.2",
            data = SystemInformation.Data(
                systemId = systemId,
                language = result.data.language,
                name = result.data.name,
                operator = result.data.operator,
                timezone = result.data.timezone,
                phoneNumber = result.data.phoneNumber,
                email = result.data.email
            )
        )
    }

    private fun mapStationInformation(result: GBFSResponse.StationsInformationResponse): StationInformation {
        return StationInformation(
            lastUpdated = result.lastUpdated,
            ttl = result.ttl.toInt(),
            version = "2.2",
            data = StationInformation.Data(
                stations = result.data.stations.stream().map {
                        StationInformation.Data.Station(
                            stationId = it.stationId,
                            name = it.name,
                            lat = it.lat.toDouble(),
                            lon = it.lon.toDouble(),
                            address = it.address,
                            capacity = it.capacity
                        )
                    }.collect(Collectors.toList()
                )
            )
        )
    }

    private fun mapStationStatus(operator: Operator, result: GBFSResponse.StationStatusesResponse): StationStatus {
        return StationStatus(
            lastUpdated = result.lastUpdated,
            ttl = result.ttl.toInt(),
            version = "2.2",
            data = StationStatus.Data(
                stations = result.data.stations.stream().map {
                    StationStatus.Station(
                        stationId = it.stationId,
                        isInstalled = it.isInstalled != 0,
                        isRenting = it.isRenting != 0,
                        isReturning = it.isReturning != 0,
                        lastReported = it.lastReported.toLong(),
                        numBikesAvailable = it.numBikesAvailable,
                        numDocksAvailable = it.numDocksAvailable,
                        vehicleTypesAvailable = listOf(
                            StationStatus.VehicleTypeAvailability(
                                vehicleTypeId = "${operator.getCodeSpace()}:VehicleType:CityBike",
                                count = it.numBikesAvailable
                            )
                        )
                    )
                }.collect(Collectors.toList())
            )
        )
    }

    private fun mapSystemPricingPlans(operator: Operator, result: GBFSResponse.SystemPricingPlans): SystemPricingPlans {
        return SystemPricingPlans(
            lastUpdated = result.lastUpdated,
            ttl = result.ttl.toInt(),
            version = "2.2",
            data = SystemPricingPlans.Data(
                plans = result.plans.stream().map {
                    SystemPricingPlans.Plan(
                        planId = "${operator.getCodeSpace()}:PricingPlan:${it.planId}",
                        name = it.name,
                        url = it.url,
                        currency = it.currency,
                        price = it.price.toFloat(),
                        isTaxable = it.isTaxable != 0,
                        description = it.description
                    )
                }.collect(Collectors.toList())
            )
        )
    }

    private fun getOperator(call: ApplicationCall): Operator? {
        return enumValueOfOrNull<Operator>(call.parameters["operator"]?.toUpperCase())
    }

    private fun getGbfsEnum(call: ApplicationCall): GbfsStandardEnum? {
        return enumValueOfOrNull<GbfsStandardEnum>(call.parameters["service"]?.toLowerCase())
    }

    private inline fun <reified T : Enum<T>> enumValueOfOrNull(name: String?): T? {
        return enumValues<T>().find { it.name == name }
    }
}
