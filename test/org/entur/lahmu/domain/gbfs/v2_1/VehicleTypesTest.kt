package org.entur.lahmu.domain.gbfs.v2_1

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class VehicleTypesTest {

    @Test
    fun testDeserialize() {
        Assertions.assertDoesNotThrow {
            Json.decodeFromString<VehicleTypes>("""
                {
                  "last_updated": 1434054678,
                  "ttl": 0,
                  "version": "3.0",
                  "data": {
                    "vehicle_types": [
                      {
                        "vehicle_type_id": "abc123",
                        "form_factor": "bicycle",
                        "propulsion_type": "human",
                        "name": "Example Basic Bike"
                      },
                      {
                        "vehicle_type_id": "def456",
                        "form_factor": "scooter",
                        "propulsion_type": "electric",
                        "name": "Example E-scooter V2",
                        "max_range_meters": 12345
                      },
                      {
                        "vehicle_type_id": "car1",
                        "form_factor": "car",
                        "propulsion_type": "combustion",
                        "name": "Foor-door Sedan",
                        "max_range_meters": 523992
                      }
                    ]
                  }
                }
            """
            )
        }
    }
}
