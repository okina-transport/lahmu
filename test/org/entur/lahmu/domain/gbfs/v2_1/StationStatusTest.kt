package org.entur.lahmu.domain.gbfs.v2_1

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class StationStatusTest {
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
                            "station_id": "station 1",
                            "is_installed": true,
                            "is_renting": true,
                            "is_returning": true,
                            "last_reported": 1434054678,
                            "num_bikes_available": 1,
                            "num_docks_available": 3,
                            "vehicle_docks_available": [{
                              "vehicle_type_ids": ["abc123"],
                              "count": 2
                            }, {
                              "vehicle_type_ids": ["def456"],
                              "count": 1
                            }],
                            "vehicle_types_available": [{
                              "vehicle_type_id": "abc123",
                              "count": 1
                            }, {
                              "vehicle_type_id": "def456",
                              "count": 0
                            }]        
                          }, {
                            "station_id": "station 2",
                            "is_installed": true,
                            "is_renting": true,
                            "is_returning": true,
                            "last_reported": 1434054678,
                            "num_bikes_available": 6,
                            "num_docks_available": 8,
                            "vehicle_docks_available": [{
                              "vehicle_type_ids": ["abc123"],
                              "count": 6
                            }, {
                              "vehicle_type_ids": ["def456"],
                              "count": 2
                            }],
                            "vehicle_types_available": [{
                              "vehicle_type_id": "abc123",
                              "count": 2
                            }, {
                              "vehicle_type_id": "def456",
                              "count": 4
                            }]
                          }
                        ]
                      }
                    }
                """.replace("\\s".toRegex(), "")
            val stationStatus = Json.decodeFromString<StationStatus>(json)
            Assertions.assertEquals(json, Json.encodeToString(stationStatus))
        }
    }
}
