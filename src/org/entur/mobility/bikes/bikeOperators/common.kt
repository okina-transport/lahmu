package org.entur.mobility.bikes.bikeOperators

import org.entur.mobility.bikes.GbfsStandardEnum
import org.entur.mobility.bikes.bikeOperators.Operator.Companion.getCodeSpace
import org.entur.mobility.bikes.getGbfsEndpoint

enum class Operator {
    OSLOBYSYKKEL, BERGENBYSYKKEL, TRONDHEIMBYSYKKEL, KOLUMBUSBYSYKKEL;

    companion object {
        fun Operator.isUrbanSharing() = this !== KOLUMBUSBYSYKKEL
        fun Operator.getCodeSpace() = when (this) {
            OSLOBYSYKKEL -> "YOS"
            BERGENBYSYKKEL -> "YBE"
            TRONDHEIMBYSYKKEL -> "YTR"
            KOLUMBUSBYSYKKEL -> "YKO"
        }

        fun Operator.getFetchUrls() = when (this) {
            OSLOBYSYKKEL -> osloBysykkelURL
            BERGENBYSYKKEL -> bergenBysykkelURL
            TRONDHEIMBYSYKKEL -> trondheimBysykkelURL
            KOLUMBUSBYSYKKEL -> kolumbusBysykkelURL
        }
    }
}

fun getOperatorsWithDiscovery(port: String, host: Int): Map<String, List<Map<String, String>>> =
    mapOf("operators" to Operator.values().map {
        mapOf("$it".toLowerCase() to getGbfsEndpoint(it, port, host)[GbfsStandardEnum.gbfs]!!)
    })

fun mapIdToNeTEx(id: String, operator: Operator) = "${operator.getCodeSpace()}:VehicleSharingParkingArea:$id"
