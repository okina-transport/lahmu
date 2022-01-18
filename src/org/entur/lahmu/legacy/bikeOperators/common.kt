package org.entur.lahmu.legacy.bikeOperators

import org.entur.lahmu.domain.gbfs.v2_2.VehicleTypes
import org.entur.lahmu.legacy.GbfsStandardEnum
import org.entur.lahmu.legacy.bikeOperators.Operator.Companion.getCodeSpace
import org.entur.lahmu.legacy.getGbfsEndpoint

enum class Operator {
    LILLESTROMBYSYKKEL;

    companion object {
        fun Operator.isJCDecaux() = this == LILLESTROMBYSYKKEL
        fun Operator.getCodeSpace() = when (this) {
            LILLESTROMBYSYKKEL -> "YLS"
        }

        fun Operator.getFetchUrls(accessToken: String = "") = when (this) {
            LILLESTROMBYSYKKEL -> lillestromBysykkelURL
        }

        fun Operator.getPropulsionType() =
            VehicleTypes.PropulsionType.HUMAN
    }
}

fun getOperatorsWithDiscovery(port: String, host: Int): Map<String, List<Map<String, String>>> =
    mapOf("operators" to Operator.values().map {
        mapOf(
            "name" to "$it".toLowerCase(),
            "url" to getGbfsEndpoint(it, port, host).getValue(GbfsStandardEnum.gbfs)
        )
    })

fun mapIdToNeTEx(id: String, operator: Operator) = "${operator.getCodeSpace()}:Station:$id"

fun getSecondsFrom(fromEpoch: Long, toEpoch: Long): Long {
    val diffMS = toEpoch - fromEpoch
    return if (diffMS > 0) diffMS / 1000 else 0
}

enum class PricePlan {
    SEASON_PASS, DAY_PASS_30, DAY_PASS_3, DAY_PASS_1, ADD_ON_PASS;

    override fun toString() = when (this) {
        SEASON_PASS -> "sesongkort"
        DAY_PASS_30 -> "30-dagerskort"
        DAY_PASS_1 -> "dagskort"
        ADD_ON_PASS -> "tilleggskort"
        DAY_PASS_3 -> "3-dagerskort"
    }
}
