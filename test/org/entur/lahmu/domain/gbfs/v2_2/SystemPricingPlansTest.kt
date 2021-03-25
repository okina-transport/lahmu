package org.entur.lahmu.domain.gbfs.v2_2

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SystemPricingPlansTest {
    @Test
    fun testDeserializeDistanceOnly() {
        Assertions.assertDoesNotThrow {
            val json = """
                    {
                      "last_updated": 1434054678,
  "ttl": 0,
  "version": "2.0",
                    "data": {

                      "plans": [{
                        "plan_id": "plan2",
                        "name": "One-Way",
                        "currency": "USD",
                        "price": 2.0,
                        "is_taxable": false,
                        "description": "Includes 10km, overage fees apply after 10km.",
                        "per_km_pricing": [
                          {
                            "start": 10,
                            "rate": 1.0,
                            "interval": 1,
                            "end": 25
                          }, 
                          {
                            "start": 25,
                            "rate": 0.5,
                            "interval": 1
                          },
                          {
                            "start": 25,
                            "rate": 3.0,
                            "interval": 5
                          }
                        ]
                      }]
                    }
}
                """.replace("\\s".toRegex(), "")
            val systemPricingPlans = Json.decodeFromString<SystemPricingPlans>(json)
            Assertions.assertEquals(json, Json.encodeToString(systemPricingPlans))
        }
    }

    @Test
    fun testDeserializeDistanceAndTime() {
        Assertions.assertDoesNotThrow {
            val json = """
                {
                  "last_updated": 1434054678,
  "ttl": 0,
  "version": "2.0",
                "data": {
                  "plans": [{
                    "plan_id": "plan3",
                    "name": "Simple Rate",
                    "currency": "CAD",
                    "price": 3.0,
                    "is_taxable": true,
                    "description": "${'$'}3 unlock fee, ${'$'}0.25 per kilometer and 0.50 per minute.",
                    "per_km_pricing": [{
                      "start": 0,
                      "rate": 0.25,
                      "interval": 1
                    }],
                    "per_min_pricing": [{
                      "start": 0,
                      "rate": 0.5,
                      "interval": 1
                    }]
                  }]
                }
                }
                """.replace("\\s".toRegex(), "")
            val systemPricingPlans = Json.decodeFromString<SystemPricingPlans>(json)
            Assertions.assertEquals(json, Json.encodeToString(systemPricingPlans))
        }
    }
}
