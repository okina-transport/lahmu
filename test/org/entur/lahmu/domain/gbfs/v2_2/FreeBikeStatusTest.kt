package org.entur.lahmu.domain.gbfs.v2_2

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FreeBikeStatusTest {
    @Test
    fun testDeserialize() {
        Assertions.assertDoesNotThrow {
            val json = """
                {
                  "last_updated": 1434054678,
                  "ttl": 0,
                  "version": "3.0",
                  "data": {
                    "bikes": [
                      {
                        "bike_id": "ghi789",
                        "last_reported": 1434054678,
                        "lat": 12.34,
                        "lon": 56.78,
                        "is_reserved": false,
                        "is_disabled": false,
                        "vehicle_type_id": "abc123"
                      }, {
                        "bike_id": "jkl012",
                        "last_reported": 1434054687,
                        "is_reserved": false,
                        "is_disabled": false,
                        "vehicle_type_id": "def456",
                        "current_range_meters": 6543.0,
                        "station_id": "86",
                        "pricing_plan_id": "plan3"
                      }
                    ]
                  }
                }
                """.replace("\\s".toRegex(), "")
            val freeBikeStatus = Json.decodeFromString<FreeBikeStatus>(json)
            Assertions.assertEquals(json, Json.encodeToString(freeBikeStatus))
        }
    }
}
