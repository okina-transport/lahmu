package org.entur.lahmu.domain.gbfs.v2_2

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class StationInformationTest {
    @Test
    fun testDeserialize() {
        Assertions.assertDoesNotThrow {
            val json = """
                {
                  "last_updated": 1434054678,
                  "ttl": 0,
                  "version": "3.0",
                  "data": {
                    "stations": [
                      {
                        "station_id": "pga",
                        "name": "Parking garage A",
                        "lat": 12.34,
                        "lon": 45.67,
                        "vehicle_type_capacity": {
                          "abc123": 7,
                          "def456": 9
                        }
                      }
                    ]
                  }
                }
            """.replace("\\s".toRegex(), "")
            val stationInformation = Json.decodeFromString<StationInformation>(json)
            Assertions.assertEquals(json, Json.encodeToString(stationInformation))
        }
    }
}
