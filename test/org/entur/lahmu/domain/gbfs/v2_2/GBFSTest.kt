package org.entur.lahmu.domain.gbfs.v2_2

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GBFSTest {
    @Test
    fun testDeserialize() {
        Assertions.assertDoesNotThrow {
            Json.decodeFromString<GBFS>("""
            {
              "last_updated": 1434054678,
              "ttl": 0,
              "version": "2.0",
              "data": {
                "en": {
                  "feeds": [
                    {
                      "name": "system_information",
                      "url": "https://www.example.com/gbfs/1/en/system_information"
                    },
                    {
                      "name": "station_information",
                      "url": "https://www.example.com/gbfs/1/en/station_information"
                    }
                  ]
                },
                "fr" : {
                  "feeds": [
                    {
                      "name": "system_information",
                      "url": "https://www.example.com/gbfs/1/fr/system_information"
                    },
                    {
                      "name": "station_information",
                      "url": "https://www.example.com/gbfs/1/fr/station_information"
                    }
                  ]
                }
              }
            }
            """
            )
        }
    }
}
